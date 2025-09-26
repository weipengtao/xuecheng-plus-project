package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.dto.MediaFilesPageQueryRequestDTO;
import com.xuecheng.media.model.dto.UploadFileResultDTO;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.property.MinioProperty;
import com.xuecheng.media.service.MediaFilesService;
import com.xuecheng.media.service.MinioService;
import com.xuecheng.utils.Md5Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaFilesServiceImpl implements MediaFilesService {

    private final MediaFilesMapper mediaFilesMapper;
    private final MediaProcessMapper mediaProcessMapper;

    private final MinioService minioService;
    private final MinioProperty minioProperty;

    @Override
    public PageResult<MediaFiles> pageQuery(PageParams pageParams, MediaFilesPageQueryRequestDTO pageQueryRequestDTO) {
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());

        String filename = pageQueryRequestDTO.getFilename();
        String fileType = pageQueryRequestDTO.getFileType();
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(filename != null, MediaFiles::getFilename, filename);
        queryWrapper.like(fileType != null, MediaFiles::getFileType, fileType);

        Page<MediaFiles> result = mediaFilesMapper.selectPage(page, queryWrapper);

        return PageResult.<MediaFiles>builder()
                .items(result.getRecords())
                .page(pageParams.getPageNo())
                .pageSize(pageParams.getPageSize())
                .counts(result.getTotal())
                .build();
    }

    @Override
    public UploadFileResultDTO uploadCourseFile(MultipartFile file) throws Exception {
        UploadFileResultDTO uploadFileResultDTO = new UploadFileResultDTO();

        String md5 = Md5Util.getFileMd5(file.getInputStream());
        MediaFiles mediaFile = mediaFilesMapper.selectById(md5);
        if (mediaFile != null) {
            BeanUtils.copyProperties(mediaFile, uploadFileResultDTO);
            return uploadFileResultDTO;
        }

        String bucket = minioProperty.getBucket().getOtherFiles();
        minioService.checkAndCreateBucket(bucket);

        String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        String filename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(filename);
        String objectName = datePath + "/" + md5 + "." + extension;
        minioService.uploadFile(bucket, objectName, file);

        mediaFile = new MediaFiles();
        mediaFile.setId(md5);
        mediaFile.setFilename(filename);
        mediaFile.setFileType("001001");
        mediaFile.setTags("课程图片");
        mediaFile.setBucket(bucket);
        mediaFile.setFilePath(objectName);
        mediaFile.setFileId(md5);
        mediaFile.setUrl("/" + bucket + "/" + objectName);
        mediaFile.setCreateDate(LocalDateTime.now());
        mediaFile.setAuditStatus("002002");
        mediaFile.setFileSize(file.getSize());
        mediaFilesMapper.insert(mediaFile);

        BeanUtils.copyProperties(mediaFile, uploadFileResultDTO);

        return uploadFileResultDTO;
    }

    @Override
    public Boolean checkFile(String md5) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(md5);
        if (mediaFiles == null) {
            return false;
        }

        String bucket = minioProperty.getBucket().getVideoFiles();
        String objectName = mediaFiles.getFilePath();

        return minioService.isObjectExist(bucket, objectName);
    }

    @Override
    public Boolean checkChunk(String md5, Integer chunk) {
        String bucket = minioProperty.getBucket().getVideoFiles();
        String objectName = md5.charAt(0) + "/" + md5.charAt(1) + "/" + md5 + "/" + chunk;
        return minioService.isObjectExist(bucket, objectName);
    }

    @Override
    public Boolean uploadChunk(MultipartFile file, String md5, Integer chunk) throws Exception {
        String bucket = minioProperty.getBucket().getVideoFiles();
        minioService.checkAndCreateBucket(bucket);

        String objectName = md5.charAt(0) + "/" + md5.charAt(1) + "/" + md5 + "/chunk/" + chunk;
        minioService.uploadFile(bucket, objectName, file);

        return true;
    }

    @Override
    @Transactional
    public Boolean mergeChunks(String filename, String fileMd5, Integer chunkTotal) throws Exception {
        String bucket = minioProperty.getBucket().getVideoFiles();

        String dir = fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/";
        List<String> sourceObjects = IntStream.range(0, chunkTotal)
                .mapToObj(i -> dir + "chunk/" + i)
                .toList();
        String objectName = dir + fileMd5 + filename.substring(filename.lastIndexOf("."));

        minioService.composeObject(bucket, objectName, sourceObjects);

        String mergedMd5 = Md5Util.getFileMd5(minioService.getObject(bucket, objectName));
        if (!Objects.equals(mergedMd5, fileMd5)) {
            log.debug("合并后的md5: {}, 与原本不符: {}", mergedMd5, fileMd5);
            minioService.removeObject(bucket, objectName);
            return false;
        }

        for (String sourceObject : sourceObjects) {
            minioService.removeObject(bucket, sourceObject);
        }

        MediaFiles mediaFile = new MediaFiles();
        mediaFile.setId(fileMd5);
        mediaFile.setFilename(filename);
        mediaFile.setFileType("001002");
        mediaFile.setTags("课程视频");
        mediaFile.setBucket(bucket);
        mediaFile.setFilePath(objectName);
        mediaFile.setFileId(fileMd5);
        mediaFile.setUrl("/" + bucket + "/" + objectName);
        mediaFile.setCreateDate(LocalDateTime.now());
        mediaFile.setAuditStatus("002003");
        mediaFile.setFileSize(minioService.getObjectSize(bucket, objectName));
        mediaFilesMapper.insert(mediaFile);

        // 只有非 mp4 格式的视频才需要处理
        if ("mp4".equalsIgnoreCase(FilenameUtils.getExtension(filename))) {
            return true;
        }

        MediaProcess mediaProcess = new MediaProcess();
        BeanUtils.copyProperties(mediaFile, mediaProcess);
        mediaProcess.setStatus("1");
        mediaProcess.setFailCount(0);
        mediaProcessMapper.insert(mediaProcess);

        return true;
    }

    @Override
    @Transactional
    public Boolean deleteById(String id) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(id);
        if (mediaFiles == null) {
            log.warn("视频不存在，视频 ID: {}", id);
            return false;
        }

        mediaFilesMapper.deleteById(id);

        String bucket = mediaFiles.getBucket();
        String objectName = mediaFiles.getFilePath();
        if (bucket == null || objectName == null) {
            return true;
        }

        // 删除 minio 中存储的视频
        try {
            minioService.removeObject(bucket, objectName);
        } catch (Exception e) {
            log.error("删除 MinIO 中存储的视频失败, 视频 ID: {}, bucket: {}, object: {}", id, bucket, objectName, e);
        }

        // TODO 删除 content 服务中的绑定关系

        return true;
    }
}

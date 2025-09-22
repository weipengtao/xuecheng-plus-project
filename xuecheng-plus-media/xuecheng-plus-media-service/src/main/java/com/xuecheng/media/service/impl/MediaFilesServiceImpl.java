package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.model.dto.MediaFilesPageQueryRequestDTO;
import com.xuecheng.media.model.dto.UploadFileResultDTO;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.property.MinioProperty;
import com.xuecheng.media.service.MediaFilesService;
import com.xuecheng.utils.Md5Util;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaFilesServiceImpl implements MediaFilesService {

    private final MediaFilesMapper mediaFilesMapper;

    private final MinioClient minioClient;
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
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }

        String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        String filename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(filename);
        String objectName = datePath + "/" + md5 + "." + extension;

        String contentType = file.getContentType() != null ? file.getContentType() : Files.probeContentType(Path.of(filename));

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(contentType)
                        .build()
        );

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
        mediaFile.setAuditStatus("002003");
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

        return isExistMinio(bucket, objectName);
    }

    @Override
    public Boolean checkChunk(String md5, Integer chunk) {
        String bucket = minioProperty.getBucket().getVideoFiles();
        String objectName = md5.charAt(0) + "/" + md5.charAt(1) + "/" + md5 + "/" + chunk;

        return isExistMinio(bucket, objectName);
    }

    @Override
    public Boolean uploadChunk(MultipartFile file, String md5, Integer chunk) throws Exception {
        String bucket = minioProperty.getBucket().getVideoFiles();
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }

        String filename = file.getOriginalFilename();
        String objectName = md5.charAt(0) + "/" + md5.charAt(1) + "/" + md5 + "/chunk/" + chunk;

        String contentType = file.getContentType() != null ? file.getContentType() : Files.probeContentType(Path.of(filename));

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(contentType)
                        .build()
        );

        return true;
    }

    @Override
    public Boolean mergeChunks(String filename, String fileMd5, Integer chunkTotal) throws Exception {
        String bucket = minioProperty.getBucket().getVideoFiles();
        String dir = fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/";

        List<ComposeSource> composeSources = Stream.iterate(0, i -> i + 1)
                .limit(chunkTotal)
                .map(i -> ComposeSource.builder()
                        .bucket(bucket)
                        .object(dir + "chunk/" + i)
                        .build())
                .toList();

        String objectName = dir + fileMd5 + filename.substring(filename.lastIndexOf("."));
        minioClient.composeObject(
                ComposeObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .sources(composeSources)
                        .build());

        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .build();
        String mergedMd5 = Md5Util.getFileMd5(minioClient.getObject(getObjectArgs));
        if (!Objects.equals(mergedMd5, fileMd5)) {
            log.debug("合并后的md5: {}, 与原本不符: {}", mergedMd5, fileMd5);
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build();
            minioClient.removeObject(removeObjectArgs);
            return false;
        }

        List<RemoveObjectArgs> removeObjectArgs = Stream.iterate(0, i -> i + 1)
                .limit(chunkTotal)
                .map(i -> RemoveObjectArgs.builder()
                        .bucket(bucket)
                        .object(dir + "chunk/" + i)
                        .build())
                .toList();
        for (RemoveObjectArgs i : removeObjectArgs) {
            minioClient.removeObject(i);
        }

        StatObjectResponse stat = minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .build()
        );

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
        mediaFile.setFileSize(stat.size());
        mediaFilesMapper.insert(mediaFile);

        return true;
    }

    @NotNull
    private Boolean isExistMinio(String bucket, String objectName) {
        try {
            // 使用statObject方法只获取对象元数据，而不下载内容
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build()
            );

            // 如果statObject成功执行，说明对象存在
            return true;
        } catch (ErrorResponseException e) {
            // 检查是否是"对象不存在"的错误
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                return false; // 对象不存在
            }

            // 其他MinIO错误
            log.error("MinIO查询失败: bucket={}, object={}, errorCode={}",
                    bucket, objectName, e.errorResponse().code(), e);
            throw new RuntimeException("MinIO查询失败: " + e.errorResponse().message(), e);
        } catch (Exception e) {
            // 其他异常（网络问题、权限问题等）
            log.error("MinIO查询失败: bucket={}, object={}", bucket, objectName, e);
            throw new RuntimeException("MinIO查询失败", e);
        }
    }
}

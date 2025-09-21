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
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

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

        String bucket = mediaFiles.getBucket();
        String objectName = mediaFiles.getFilePath();

        return isExistMinio(bucket, objectName);
    }

    @Override
    public Boolean checkChunk(String md5, Integer chunk) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(md5);
        if (mediaFiles == null) {
            return false;
        }

        String bucket = mediaFiles.getBucket();
        String objectName =  md5.charAt(0) + "/" + md5.charAt(1) + "/" + md5 + "/" + chunk;

        return isExistMinio(bucket, objectName);
    }

    @NotNull
    private Boolean isExistMinio(String bucket, String objectName) {
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .build();
        try {
            GetObjectResponse object = minioClient.getObject(getObjectArgs);
            if (object == null) {
                return false;
            }
        } catch (Exception e) {
            log.error("向 minio 查询失败, {}", e.getMessage());
            throw new RuntimeException("向 minio 查询失败");
        }
        return true;
    }
}

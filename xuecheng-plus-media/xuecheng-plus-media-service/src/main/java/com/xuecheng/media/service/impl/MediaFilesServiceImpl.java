package com.xuecheng.media.service.impl;

import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.model.dto.UploadFileResultDTO;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.property.MinioProperty;
import com.xuecheng.media.service.MediaFilesService;
import com.xuecheng.utils.Md5Util;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class MediaFilesServiceImpl implements MediaFilesService {

    private final MediaFilesMapper mediaFilesMapper;

    private final MinioClient minioClient;
    private final MinioProperty minioProperty;

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
}

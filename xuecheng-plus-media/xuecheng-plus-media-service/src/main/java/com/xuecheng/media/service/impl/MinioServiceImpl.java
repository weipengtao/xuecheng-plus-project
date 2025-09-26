package com.xuecheng.media.service.impl;

import com.xuecheng.media.service.MinioService;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;

    @Override
    public void checkAndCreateBucket(String bucket) throws Exception {
        boolean found = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucket).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    @Override
    public void uploadFile(String bucket, String objectName, MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();
        String contentType = null;
        if (filename != null) {
            contentType = file.getContentType() != null
                    ? file.getContentType()
                    : Files.probeContentType(Path.of(filename));
        }

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(contentType)
                        .build()
        );
    }

    @Override
    public void uploadFile(String bucket, String objectName, File file) throws Exception {
        String contentType = Files.probeContentType(file.toPath());

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .stream(Files.newInputStream(file.toPath()), file.length(), -1)
                        .contentType(contentType != null ? contentType : "application/octet-stream")
                        .build()
        );
    }


    @Override
    public InputStream getObject(String bucket, String objectName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .build()
        );
    }

    @Override
    public void downloadFile(String bucket, String objectName, String localFilePath) throws Exception {
        minioClient.downloadObject(
                DownloadObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .filename(localFilePath)
                        .build()
        );
    }

    @Override
    public File downloadTempFile(String bucket, String objectName) throws Exception {
        File tempFile = File.createTempFile("minio-", ".tmp");

        // 删除占位文件
        if (tempFile.exists()) {
            tempFile.delete();
        }

        this.downloadFile(bucket, objectName, tempFile.getAbsolutePath());
        return tempFile;
    }

    @Override
    public void removeObject(String bucket, String objectName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .build()
        );
    }

    @Override
    public boolean isObjectExist(String bucket, String objectName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build()
            );
            return true;
        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                return false;
            }
            log.error("MinIO 查询失败: bucket={}, object={}, errorCode={}",
                    bucket, objectName, e.errorResponse().code(), e);
            throw new RuntimeException("MinIO 查询失败: " + e.errorResponse().message(), e);
        } catch (Exception e) {
            log.error("MinIO 查询失败: bucket={}, object={}", bucket, objectName, e);
            throw new RuntimeException("MinIO 查询失败", e);
        }
    }

    @Override
    public void composeObject(String bucket, String targetObject, List<String> sourceObjects) throws Exception {
        List<ComposeSource> sources = sourceObjects.stream()
                .map(obj -> ComposeSource.builder()
                        .bucket(bucket)
                        .object(obj)
                        .build())
                .toList();

        minioClient.composeObject(
                ComposeObjectArgs.builder()
                        .bucket(bucket)
                        .object(targetObject)
                        .sources(sources)
                        .build()
        );
    }

    @Override
    public long getObjectSize(String bucket, String objectName) throws Exception {
        StatObjectResponse statObject = minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .build()
        );
        return statObject.size();
    }
}

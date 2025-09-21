package com.xuecheng.media.controller;

import com.xuecheng.media.property.MinioProperty;
import com.xuecheng.utils.Md5Util;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@SpringBootTest
class MediaFilesControllerTest {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioProperty minioProperty;

    @Test
    void testMultipartFile() throws IOException {
        ClassPathResource resource = new ClassPathResource("a.txt");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                resource.getFilename(),
                Files.probeContentType(resource.getFile().toPath()),
                resource.getInputStream()
        );
        System.out.println(file.getContentType());
        System.out.println(file.getName());
        System.out.println(file.getSize());
        System.out.println(file.getOriginalFilename());
        System.out.println(Arrays.toString(file.getBytes()));
    }


    @Test
    void testUpload() throws Exception {
        ClassPathResource resource = new ClassPathResource("a.jpg");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                resource.getFilename(),
                Files.probeContentType(resource.getFile().toPath()),
                resource.getInputStream()
        );

        String bucket = minioProperty.getBucket().getOtherFiles();
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }

        String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        String md5 = Md5Util.getFileMd5(file.getInputStream());
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
        System.out.println(objectName);
    }
}
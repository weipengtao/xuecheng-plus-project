package com.xuecheng.media.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface MinioService {

    /**
     * 检查桶是否存在，不存在则创建
     */
    void checkAndCreateBucket(String bucket) throws Exception;

    /**
     * 上传文件
     */
    void uploadFile(String bucket, String objectName, MultipartFile file) throws Exception;

    void uploadFile(String bucket, String objectName, File file) throws Exception;

    /**
     * 获取文件流
     */
    InputStream getObject(String bucket, String objectName) throws Exception;

    /**
     * 下载到本地文件
     */
    void downloadFile(String bucket, String objectName, String localFilePath) throws Exception;

    /**
     * 下载为临时文件
     */
    File downloadTempFile(String bucket, String objectName) throws Exception;

    /**
     * 删除对象
     */
    void removeObject(String bucket, String objectName) throws Exception;

    /**
     * 判断对象是否存在
     */
    boolean isObjectExist(String bucket, String objectName);

    /**
     * 合并多个对象
     */
    void composeObject(String bucket, String targetObject, List<String> sourceObjects) throws Exception;

    /**
     * 获取对象大小
     */
    long getObjectSize(String bucket, String objectName) throws Exception;
}

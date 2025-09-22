package com.xuecheng.media.controller;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.MediaFilesPageQueryRequestDTO;
import com.xuecheng.media.model.dto.UploadFileResultDTO;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFilesService;
import io.minio.errors.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
@Tag(name = "媒资管理接口", description = "实现媒资管理相关操作")
public class MediaFilesController {

    private final MediaFilesService mediaFilesService;

    @PostMapping("/files")
    @Operation(summary = "课程查询接口", description = "分页查询课程信息")
    public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody(required = false) MediaFilesPageQueryRequestDTO pageQueryRequestDTO) {
        return mediaFilesService.pageQuery(pageParams, pageQueryRequestDTO);
    }

    @PostMapping("/upload/coursefile")
    @Operation(summary = "上传课程图片接口", description = "上传课程图片")
    public UploadFileResultDTO uploadCourseFile(MultipartFile filedata) throws Exception {
        return mediaFilesService.uploadCourseFile(filedata);
    }

    @PostMapping("/upload/checkfile")
    @Operation(summary = "检查文件是否上传接口", description = "检查文件是否上传")
    public RestResponse<Boolean> checkFile(String fileMd5) {
        return RestResponse.success(mediaFilesService.checkFile(fileMd5));
    }

    @PostMapping("/upload/checkchunk")
    @Operation(summary = "检查文件分块是否上传接口", description = "检查文件分块是否上传")
    public RestResponse<Boolean> checkChunk(String fileMd5, Integer chunk) {
        return RestResponse.success(mediaFilesService.checkChunk(fileMd5, chunk));
    }

    @PostMapping("/upload/uploadchunk")
    @Operation(summary = "上传文件块接口", description = "上传文件块")
    public RestResponse<Boolean> uploadChunk(MultipartFile file, String fileMd5, Integer chunk) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return RestResponse.success(mediaFilesService.uploadChunk(file, fileMd5, chunk));
    }
}

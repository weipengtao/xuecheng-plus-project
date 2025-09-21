package com.xuecheng.media.service;

import com.xuecheng.media.model.dto.UploadFileResultDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MediaFilesService {
    UploadFileResultDTO uploadCourseFile(MultipartFile file) throws Exception;
}

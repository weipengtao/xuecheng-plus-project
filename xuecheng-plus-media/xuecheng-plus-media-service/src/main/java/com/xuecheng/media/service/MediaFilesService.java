package com.xuecheng.media.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.model.dto.MediaFilesPageQueryRequestDTO;
import com.xuecheng.media.model.dto.UploadFileResultDTO;
import com.xuecheng.media.model.po.MediaFiles;
import org.springframework.web.multipart.MultipartFile;

public interface MediaFilesService {
    PageResult<MediaFiles> pageQuery(PageParams pageParams, MediaFilesPageQueryRequestDTO pageQueryRequestDTO);

    UploadFileResultDTO uploadCourseFile(MultipartFile file) throws Exception;

    Boolean checkFile(String md5);

    Boolean checkChunk(String md5, Integer chunk);

    Boolean uploadChunk(MultipartFile file, String md5, Integer chunk) throws Exception;

    Boolean mergeChunks(String filename, String fileMd5, Integer chunkTotal) throws Exception;
}

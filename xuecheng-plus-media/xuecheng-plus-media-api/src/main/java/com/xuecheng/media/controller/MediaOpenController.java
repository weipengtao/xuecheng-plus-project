package com.xuecheng.media.controller;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFilesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/open")
@RequiredArgsConstructor
public class MediaOpenController {

    private final MediaFilesService mediaFilesService;

    @GetMapping("/preview/{mediaId}")
    public RestResponse<String> getPlayUrl(@PathVariable("mediaId") String mediaId) {
        MediaFiles mediaFiles = mediaFilesService.getById(mediaId);
        return RestResponse.success(mediaFiles.getUrl());
    }
}

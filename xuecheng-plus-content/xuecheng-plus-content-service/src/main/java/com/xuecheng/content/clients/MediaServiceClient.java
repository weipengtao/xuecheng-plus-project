package com.xuecheng.content.clients;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.content.config.FeignMultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@FeignClient(value = "media-service", path = "/media", configuration = FeignMultipartSupportConfig.class)
public interface MediaServiceClient {

    @PostMapping(value = "/upload/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    RestResponse<Boolean> uploadOtherFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam("objectName") String objectName
    );
}

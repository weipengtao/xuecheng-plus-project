package com.xuecheng.content.clients.fallback;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.content.clients.MediaServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MediaServiceFallbackFactory implements FallbackFactory<MediaServiceClient> {
    @Override
    public MediaServiceClient create(Throwable cause) {
        return (file, objectName) -> {
            log.debug("调用媒资服务失败: {}", cause.getMessage());
            return RestResponse.error(false, "调用媒资服务失败" + cause.getMessage());
        };
    }
}

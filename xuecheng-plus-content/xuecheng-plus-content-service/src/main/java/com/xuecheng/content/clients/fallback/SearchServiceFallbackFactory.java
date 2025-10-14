package com.xuecheng.content.clients.fallback;

import com.xuecheng.content.clients.SearchServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SearchServiceFallbackFactory implements FallbackFactory<SearchServiceClient> {
    @Override
    public SearchServiceClient create(Throwable cause) {
        return courseDoc -> {
            log.debug("调用搜索服务失败: {}", cause.getMessage());
            return false;
        };
    }
}

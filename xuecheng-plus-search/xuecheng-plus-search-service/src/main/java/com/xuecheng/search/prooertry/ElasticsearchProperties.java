package com.xuecheng.search.prooertry;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticsearchProperties {
    /**
     * 连接协议
     */
    private String scheme;

    /**
     * es 集群节点配置
     */
    private List<HostConfig> hosts;

    /**
     * 各业务索引名称
     */
    private Index index;

    /**
     * source 过滤字段配置
     */
    private SourceFields sourceFields;

    @Data
    public static class HostConfig {
        /**
         * 主机地址
         */
        private String host;
        /**
         * 端口
         */
        private int port;
    }

    @Data
    public static class Index {
        /**
         * 课程索引
         */
        private String course;
    }

    @Data
    public static class SourceFields {
        /**
         * 课程索引返回字段
         */
        private List<String> course;
    }
}

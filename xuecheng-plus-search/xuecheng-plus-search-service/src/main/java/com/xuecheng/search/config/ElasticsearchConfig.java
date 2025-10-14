package com.xuecheng.search.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.xuecheng.search.prooertry.ElasticsearchProperties;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ElasticsearchConfig {

    private final ElasticsearchProperties properties;

    @Bean(destroyMethod = "close")
    public ElasticsearchClient elasticsearchClient() {
        List<HttpHost> httpHosts = properties.getHosts().stream()
                .map(h -> new HttpHost(h.getHost(), h.getPort(), properties.getScheme()))
                .toList();

        RestClientBuilder builder = RestClient.builder(httpHosts.toArray(new HttpHost[0]));
        RestClient restClient = builder.build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper(mapper));

        return new ElasticsearchClient(transport);
    }
}

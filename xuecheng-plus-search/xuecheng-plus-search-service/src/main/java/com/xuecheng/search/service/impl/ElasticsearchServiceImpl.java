package com.xuecheng.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.xuecheng.search.service.ElasticsearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ElasticsearchServiceImpl implements ElasticsearchService {

    private final ElasticsearchClient client;

    // -------------------- 索引操作 --------------------
    @Override
    public boolean deleteIndex(String indexName) {
        try {
            BooleanResponse exists = client.indices().exists(e -> e.index(indexName));
            if (!exists.value()) return false;
            DeleteIndexResponse response = client.indices().delete(d -> d.index(indexName));
            return response.acknowledged();
        } catch (IOException e) {
            log.error("删除索引 [{}] 失败", indexName, e);
            return false;
        }
    }

    @Override
    public boolean indexExists(String indexName) {
        try {
            return client.indices().exists(e -> e.index(indexName)).value();
        } catch (IOException e) {
            log.error("检查索引 [{}] 是否存在失败", indexName, e);
            return false;
        }
    }

    // -------------------- 文档操作 --------------------

    @Override
    public <T> boolean indexDocument(String index, String id, T document) {
        try {
            IndexResponse response = client.index(i -> i
                    .index(index)
                    .id(id)
                    .document(document)
            );
            String result = response.result().jsonValue();
            return "created".equalsIgnoreCase(result) || "updated".equalsIgnoreCase(result);
        } catch (IOException e) {
            log.error("文档索引失败 [{}:{}]", index, id, e);
            return false;
        }
    }

    @Override
    public <T> T getDocumentById(String index, String id, Class<T> clazz) {
        try {
            GetResponse<T> response = client.get(g -> g.index(index).id(id), clazz);
            return response.found() ? response.source() : null;
        } catch (IOException e) {
            log.error("获取文档失败 [{}:{}]", index, id, e);
            return null;
        }
    }

    @Override
    public boolean deleteDocumentById(String index, String id) {
        try {
            DeleteResponse response = client.delete(d -> d.index(index).id(id));
            return "deleted".equalsIgnoreCase(response.result().jsonValue());
        } catch (IOException e) {
            log.error("删除文档失败 [{}:{}]", index, id, e);
            return false;
        }
    }

    // -------------------- 查询操作 --------------------

    @Override
    public <T> List<T> matchSearch(String index, String field, String value, Class<T> clazz) {
        try {
            Query query = MatchQuery.of(m -> m.field(field).query(value))._toQuery();
            SearchResponse<T> response = client.search(s -> s.index(index).query(query), clazz);
            return extractHits(response);
        } catch (IOException e) {
            log.error("match 查询失败 [{}:{}={}]", index, field, value, e);
            return new ArrayList<>();
        }
    }

    @Override
    public <T> List<T> termSearch(String index, String field, String value, Class<T> clazz) {
        try {
            Query query = TermQuery.of(t -> t.field(field).value(value))._toQuery();
            SearchResponse<T> response = client.search(s -> s.index(index).query(query), clazz);
            return extractHits(response);
        } catch (IOException e) {
            log.error("term 查询失败 [{}:{}={}]", index, field, value, e);
            return new ArrayList<>();
        }
    }

    @Override
    public <T> List<T> matchSearchPage(String index, String field, String value, int page, int size, Class<T> clazz) {
        try {
            Query query = MatchQuery.of(m -> m.field(field).query(value))._toQuery();
            SearchResponse<T> response = client.search(s -> s.index(index)
                    .query(query)
                    .from((page - 1) * size)
                    .size(size), clazz);
            return extractHits(response);
        } catch (IOException e) {
            log.error("分页 match 查询失败 [{}:{}={}]", index, field, value, e);
            return new ArrayList<>();
        }
    }

    /**
     * 提取查询结果
     */
    private <T> List<T> extractHits(SearchResponse<T> response) {
        List<T> results = new ArrayList<>();
        if (response.hits() != null && response.hits().hits() != null) {
            for (Hit<T> hit : response.hits().hits()) {
                results.add(hit.source());
            }
        }
        return results;
    }
}

package com.xuecheng.search.service;

import java.util.List;

public interface ElasticsearchService {
    // -------------------- 索引操作 --------------------
    /** 删除索引 */
    boolean deleteIndex(String indexName);

    /** 判断索引是否存在 */
    boolean indexExists(String indexName);

    // -------------------- 文档操作 --------------------

    /** 新增或更新文档 */
    <T> boolean indexDocument(String index, String id, T document);

    /** 根据 ID 获取文档 */
    <T> T getDocumentById(String index, String id, Class<T> clazz);

    /** 删除文档 */
    boolean deleteDocumentById(String index, String id);

    // -------------------- 查询操作 --------------------

    /** match 查询（全文匹配） */
    <T> List<T> matchSearch(String index, String field, String value, Class<T> clazz);

    /** term 查询（精确匹配） */
    <T> List<T> termSearch(String index, String field, String value, Class<T> clazz);

    /** 分页 match 查询 */
    <T> List<T> matchSearchPage(String index, String field, String value, int page, int size, Class<T> clazz);
}

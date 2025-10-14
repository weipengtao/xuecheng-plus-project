package com.xuecheng.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.xuecheng.search.model.po.CourseDoc;
import com.xuecheng.search.prooertry.ElasticsearchProperties;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.LocalDateTime;

@SpringBootTest
@Disabled
class ElasticsearchClientTest {

    @Autowired
    ElasticsearchClient elasticsearchClient;

    @Autowired
    ElasticsearchProperties properties;

    @Test
    public void testAddDoc() throws IOException {
        String courseIndex = properties.getIndex().getCourse();

        CourseDoc doc = new CourseDoc();
        doc.setId(1003L);
        doc.setCompanyId(10L);
        doc.setCompanyName("学成在线");
        doc.setName("Java 从入门到精通 Java 实战课程");
        doc.setUsers("编程初学者");
        doc.setTags("Java,SpringBoot,后端开发");
        doc.setMt("program");
        doc.setMtName("编程开发");
        doc.setSt("java");
        doc.setStName("Java开发");
        doc.setGrade("初级");
        doc.setTeachmode("在线");
        doc.setPic("https://file.51xuecheng.cn/java.png");
        doc.setDescription("学习 Java 基础，掌握 Java 高级用法，通过 Java 项目实战巩固知识。");
        doc.setCreateDate(LocalDateTime.now());
        doc.setStatus("published");
        doc.setRemark("测试多次匹配");
        doc.setCharge("201001");
        doc.setPrice(99.0f);
        doc.setOriginalPrice(199.0f);
        doc.setValidDays(365);

        IndexResponse response = elasticsearchClient.index(i -> i
                .index(courseIndex)
                .id(doc.getId().toString())
                .document(doc));

        System.out.println(response.result());
    }

    @SneakyThrows
    @Test
    public void testMatchAll() {
        SearchResponse<CourseDoc> response = elasticsearchClient.search(s -> s
                        .index(properties.getIndex().getCourse())
                        .query(q -> q
                                .matchAll(m -> m)),
                CourseDoc.class);
        response.hits().hits().forEach(hit -> System.out.println(hit.source()));
    }
}
package com.xuecheng.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import com.xuecheng.search.model.dto.CourseSearchParamDTO;
import com.xuecheng.search.model.dto.CourseSearchResultDTO;
import com.xuecheng.search.model.po.CourseDoc;
import com.xuecheng.search.prooertry.ElasticsearchProperties;
import com.xuecheng.search.service.CourseSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseSearchServiceImpl implements CourseSearchService {

    private final ElasticsearchProperties properties;

    private final ElasticsearchClient client;

    @Override
    public CourseSearchResultDTO<CourseDoc> list(CourseSearchParamDTO courseSearchParamDTO) {
        String courseIndex = properties.getIndex().getCourse();
        List<String> courseFields = properties.getSourceFields().getCourse();

        long pageNo = courseSearchParamDTO.getPageNo();
        long pageSize = courseSearchParamDTO.getPageSize();
        int from = Math.toIntExact((pageNo - 1) * pageSize);

        SearchResponse<CourseDoc> response;
        try {
            response = client.search(s -> s
                            .index(courseIndex)
                            .source(src -> src.filter(f -> f.includes(courseFields)))
                            .query(q -> q
                                    .bool(b -> {
                                        if (StringUtils.isNotEmpty(courseSearchParamDTO.getKeywords())) {
                                            b.must(m -> m
                                                    .multiMatch(mm -> mm
                                                            .query(courseSearchParamDTO.getKeywords())
                                                            .fields("name^10", "description")
                                                            .minimumShouldMatch("70%")
                                                    )
                                            );
                                        }

                                        if (StringUtils.isNotEmpty(courseSearchParamDTO.getMt())) {
                                            b.must(m -> m
                                                    .term(t -> t.field("mtName").value(courseSearchParamDTO.getMt()))
                                            );
                                        }

                                        if (StringUtils.isNotEmpty(courseSearchParamDTO.getSt())) {
                                            b.must(m -> m
                                                    .term(t -> t.field("stName").value(courseSearchParamDTO.getSt()))
                                            );
                                        }

                                        if (StringUtils.isNotEmpty(courseSearchParamDTO.getGrade())) {
                                            b.must(m -> m
                                                    .term(t -> t.field("grade").value(courseSearchParamDTO.getGrade()))
                                            );
                                        }

                                        return b;
                                    })
                            )
                            .from(from)
                            .size(Math.toIntExact(pageSize))
                            .aggregations("mtAgg", a -> a.terms(t -> t.field("mtName").size(100)))
                            .aggregations("stAgg", a -> a.terms(t -> t.field("stName").size(100)))
                            .highlight(h -> h
                                    .fields("name", f -> f
                                            .preTags("<span style='color:red'>")
                                            .postTags("</span>"))
                            ),
                    CourseDoc.class
            );
        } catch (IOException e) {
            log.error("课程搜索失败,参数:{}", courseSearchParamDTO, e);
            throw new RuntimeException(e);
        }

        List<Hit<CourseDoc>> hits = response.hits().hits();
        List<CourseDoc> items = new ArrayList<>();

        for (Hit<CourseDoc> hit : hits) {
            CourseDoc doc = hit.source();

            if (doc != null && hit.highlight() != null && hit.highlight().get("name") != null) {
                String highlightName = String.join(",", hit.highlight().get("name"));
                doc.setName(highlightName);
            }

            items.add(doc);
        }
        long counts = response.hits().total() == null ? 0 : response.hits().total().value();

        List<String> mtList = response.aggregations().get("mtAgg").sterms().buckets().array().stream()
                .map(b -> b.key().stringValue())
                .toList();
        List<String> stList = response.aggregations().get("stAgg").sterms().buckets().array().stream()
                .map(b -> b.key().stringValue())
                .toList();

        return new CourseSearchResultDTO<>(items, counts, pageNo, pageSize, mtList, stList);
    }
}

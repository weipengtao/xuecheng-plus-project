package com.xuecheng.search.service.impl;

import com.xuecheng.search.model.po.CourseDoc;
import com.xuecheng.search.prooertry.ElasticsearchProperties;
import com.xuecheng.search.service.CourseIndexService;
import com.xuecheng.search.service.ElasticsearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseIndexServiceImpl implements CourseIndexService {

    private final ElasticsearchProperties properties;

    private final ElasticsearchService elasticsearchService;

    @Override
    public Boolean indexDocument(CourseDoc courseDoc) {
        return elasticsearchService.indexDocument(properties.getIndex().getCourse(), courseDoc.getId().toString(), courseDoc);
    }
}

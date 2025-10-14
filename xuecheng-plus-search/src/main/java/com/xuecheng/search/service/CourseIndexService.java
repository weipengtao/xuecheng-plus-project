package com.xuecheng.search.service;

import com.xuecheng.search.model.po.CourseDoc;

public interface CourseIndexService {
    Boolean indexDocument(CourseDoc courseDoc);
}

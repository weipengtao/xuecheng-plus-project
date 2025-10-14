package com.xuecheng.search.service;

import com.xuecheng.search.model.dto.CourseSearchParamDTO;
import com.xuecheng.search.model.dto.CourseSearchResultDTO;
import com.xuecheng.search.model.po.CourseDoc;

public interface CourseSearchService {
    CourseSearchResultDTO<CourseDoc> list(CourseSearchParamDTO courseSearchParamDTO);
}

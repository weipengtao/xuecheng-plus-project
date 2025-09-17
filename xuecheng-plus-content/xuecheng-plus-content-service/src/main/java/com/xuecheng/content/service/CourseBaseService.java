package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.QueryCourseParamsDTO;
import com.xuecheng.content.model.po.CourseBase;

public interface CourseBaseService {
    PageResult<CourseBase> pageList(PageParams pageParams, QueryCourseParamsDTO queryCourseParamsDTO);
}

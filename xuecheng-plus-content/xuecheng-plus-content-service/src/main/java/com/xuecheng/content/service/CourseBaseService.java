package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDTO;
import com.xuecheng.content.model.dto.CourseBaseInfoDTO;
import com.xuecheng.content.model.dto.EditCourseDTO;
import com.xuecheng.content.model.dto.QueryCourseParamsDTO;
import com.xuecheng.content.model.po.CourseBase;

public interface CourseBaseService {
    PageResult<CourseBase> pageList(PageParams pageParams, QueryCourseParamsDTO queryCourseParamsDTO);

    CourseBaseInfoDTO createCourseBase(Long companyId, AddCourseDTO addCourseDTO);

    CourseBaseInfoDTO getCourseBaseInfoById(Long id);

    CourseBaseInfoDTO updateCourseBase(Long companyId, EditCourseDTO editCourseDTO);
}

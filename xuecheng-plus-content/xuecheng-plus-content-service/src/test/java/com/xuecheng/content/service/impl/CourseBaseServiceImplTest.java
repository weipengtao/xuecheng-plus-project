package com.xuecheng.content.service.impl;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.QueryCourseParamsDTO;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CourseBaseServiceImplTest {

    @Autowired
    private CourseBaseService courseBaseService;

    @Test
    public void testPageList() {
        PageParams pageParams = new PageParams();
        pageParams.setPageNo(2L);
        pageParams.setPageSize(3L);

        QueryCourseParamsDTO queryCourseParamsDTO = new QueryCourseParamsDTO();
        queryCourseParamsDTO.setCourseName("java");

        PageResult<CourseBase> courseBasePageResult = courseBaseService.pageList(pageParams, queryCourseParamsDTO);
        Assertions.assertNotNull(courseBasePageResult);
    }
}
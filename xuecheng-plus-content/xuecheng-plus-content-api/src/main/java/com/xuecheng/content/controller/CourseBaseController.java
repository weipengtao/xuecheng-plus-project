package com.xuecheng.content.controller;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.QueryCourseParamsDTO;
import com.xuecheng.content.model.po.CourseBase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content")
@Tag(name = "课程基本信息管理接口", description = "课程基本信息相关操作")
public class CourseBaseController {

    @PostMapping("/course/list")
    @Operation(summary = "课程查询接口", description = "分页查询课程信息")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDTO queryCourseParamsDTO) {
        return null;
    }
}

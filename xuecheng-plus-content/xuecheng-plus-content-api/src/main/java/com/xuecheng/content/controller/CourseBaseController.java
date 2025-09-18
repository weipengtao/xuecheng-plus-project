package com.xuecheng.content.controller;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDTO;
import com.xuecheng.content.model.dto.CourseBaseInfoDTO;
import com.xuecheng.content.model.dto.QueryCourseParamsDTO;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/content/course")
@RequiredArgsConstructor
@Tag(name = "课程基本信息管理接口", description = "课程基本信息相关操作")
public class CourseBaseController {

    private final CourseBaseService courseBaseService;

    @PostMapping("/list")
    @Operation(summary = "课程查询接口", description = "分页查询课程信息")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDTO queryCourseParamsDTO) {
        return courseBaseService.pageList(pageParams, queryCourseParamsDTO);
    }

    @PostMapping
    @Operation(summary = "课程添加接口", description = "新增课程信息")
    public CourseBaseInfoDTO create(@RequestBody AddCourseDTO addCourseDTO) {
        Long companyId = 1232141425L; // TODO 机构ID, 目前硬编码
        return courseBaseService.createCourseBase(companyId, addCourseDTO);
    }
}

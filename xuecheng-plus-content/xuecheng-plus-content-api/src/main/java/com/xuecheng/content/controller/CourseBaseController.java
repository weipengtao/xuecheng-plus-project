package com.xuecheng.content.controller;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDTO;
import com.xuecheng.content.model.dto.CourseBaseInfoDTO;
import com.xuecheng.content.model.dto.EditCourseDTO;
import com.xuecheng.content.model.dto.QueryCourseParamsDTO;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/course")
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

    @PreAuthorize("hasAuthority('xc_sysmanager')")
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询课程信息", description = "根据课程ID查询课程的基本信息和营销信息")
    public CourseBaseInfoDTO getById(@PathVariable Long id) {
        return courseBaseService.getCourseBaseInfoById(id);
    }

    @PutMapping
    @Operation(summary = "课程修改接口", description = "修改课程信息")
    public CourseBaseInfoDTO updateById(@RequestBody EditCourseDTO editCourseDTO) {
        Long companyId = 1232141425L; // TODO 机构ID, 目前硬编码
        return courseBaseService.updateCourseBase(companyId, editCourseDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "课程删除接口", description = "根据ID删除课程信息")
    public void delete(@PathVariable Long id) {
        courseBaseService.deleteById(id);
    }
}

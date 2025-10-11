package com.xuecheng.content.controller;

import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courseTeacher")
@RequiredArgsConstructor
@Tag(name = "课程教师接口", description = "课程教师相关的操作接口")
public class CourseTeacherController {

    private final CourseTeacherService courseTeacherService;

    @GetMapping("/list/{courseId}")
    @Operation(summary = "根据课程ID查询课程教师信息", description = "根据课程ID查询课程教师信息")
    public List<CourseTeacher> listByCourseId(@PathVariable Long courseId) {
        return courseTeacherService.listByCourseId(courseId);
    }

    @PostMapping
    @Operation(summary = "添加课程教师信息", description = "添加课程教师信息")
    public CourseTeacher save(@RequestBody CourseTeacher courseTeacher) {
        return courseTeacherService.save(courseTeacher);
    }

    @DeleteMapping("/course/{courseId}/{id}")
    @Operation(summary = "删除课程教师信息", description = "根据ID删除课程教师信息")
    public void delete(@PathVariable Long courseId, @PathVariable Long id) {
        courseTeacherService.delete(courseId, id);
    }
}

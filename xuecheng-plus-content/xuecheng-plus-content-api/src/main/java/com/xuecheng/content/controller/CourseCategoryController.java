package com.xuecheng.content.controller;

import com.xuecheng.content.model.dto.CourseCategoryTreeDTO;
import com.xuecheng.content.service.CourseCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/course-category")
@RequiredArgsConstructor
@Tag(name = "课程分类信息接口", description = "课程分类信息相关操作")
public class CourseCategoryController {

    private final CourseCategoryService courseCategoryService;

    @GetMapping("/tree-nodes")
    @Operation(summary = "课程分类查询接口", description = "以树形结构返回所有课程分类")
    public List<CourseCategoryTreeDTO> getTreeNodes() {
        return courseCategoryService.getTreeNodes("1");
    }
}

package com.xuecheng.content.controller;

import com.xuecheng.content.model.dto.TeachplanTreeNodeDTO;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content/teachplan")
@RequiredArgsConstructor
@Tag(name = "课程计划接口", description = "课程计划相关的操作接口")
public class TeachplanController {

    private final TeachplanService teachplanService;

    @GetMapping("/{courseId}/tree-nodes")
    public List<TeachplanTreeNodeDTO> getTeachplanTreeNodes(@PathVariable String courseId) {
        return teachplanService.getTeachplanTreeNodesByCourseId(courseId);
    }
}

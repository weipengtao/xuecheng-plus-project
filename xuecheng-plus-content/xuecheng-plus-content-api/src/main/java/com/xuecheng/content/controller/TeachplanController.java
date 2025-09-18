package com.xuecheng.content.controller;

import com.xuecheng.content.model.dto.EditTeachplanDTO;
import com.xuecheng.content.model.dto.TeachplanTreeNodeDTO;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/content/teachplan")
@RequiredArgsConstructor
@Tag(name = "课程计划接口", description = "课程计划相关的操作接口")
public class TeachplanController {

    private final TeachplanService teachplanService;

    @GetMapping("/{courseId}/tree-nodes")
    @Operation(summary = "获取课程计划树形结构", description = "根据课程ID获取对应的课程计划树形结构")
    public List<TeachplanTreeNodeDTO> getTeachplanTreeNodes(@PathVariable String courseId) {
        return teachplanService.getTeachplanTreeNodesByCourseId(courseId);
    }

    @PostMapping
    @Operation(summary = "创建或修改课程计划", description = "创建或修改课程计划")
    public void addOrUpdateTeachplan(@RequestBody EditTeachplanDTO editTeachplanDTO) {
        teachplanService.addOrUpdateTeachplan(editTeachplanDTO);
    }
}

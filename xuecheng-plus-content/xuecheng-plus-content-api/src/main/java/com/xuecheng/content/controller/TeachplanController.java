package com.xuecheng.content.controller;

import com.xuecheng.content.model.dto.AssociateMediaDTO;
import com.xuecheng.content.model.dto.EditTeachplanDTO;
import com.xuecheng.content.model.dto.TeachplanTreeNodeDTO;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teachplan")
@RequiredArgsConstructor
@Tag(name = "课程计划接口", description = "课程计划相关的操作接口")
public class TeachplanController {

    private final TeachplanService teachplanService;

    @GetMapping("/{courseId}/tree-nodes")
    @Operation(summary = "获取课程计划树形结构", description = "根据课程ID获取对应的课程计划树形结构")
    public List<TeachplanTreeNodeDTO> getTeachplanTreeNodes(@PathVariable Long courseId) {
        return teachplanService.getTeachplanTreeNodesByCourseId(courseId);
    }

    @PostMapping
    @Operation(summary = "创建或修改课程计划", description = "创建或修改课程计划")
    public void addOrUpdateTeachplan(@RequestBody EditTeachplanDTO editTeachplanDTO) {
        teachplanService.addOrUpdateTeachplan(editTeachplanDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除课程计划", description = "根据课程计划ID删除对应的课程计划")
    public void deleteTeachplan(@PathVariable Long id) {
        teachplanService.deleteTeachplanById(id);
    }

    @PostMapping("/movedown/{id}")
    @Operation(summary = "课程计划下移", description = "课程计划下移")
    public void moveDown(@PathVariable Long id) {
        teachplanService.moveDown(id);
    }

    @PostMapping("/moveup/{id}")
    @Operation(summary = "课程计划上移", description = "课程计划上移")
    public void moveUp(@PathVariable Long id) {
        teachplanService.moveUp(id);
    }

    @PostMapping("/association/media")
    @Operation(summary = "课程计划绑定媒资", description = "课程计划绑定媒资")
    public TeachplanMedia associateMedia(@RequestBody AssociateMediaDTO associateMediaDTO) {
        return teachplanService.associateMedia(associateMediaDTO);
    }

    @DeleteMapping("/association/media/{teachPlanId}/{mediaId}")
    @Operation(summary = "课程计划解绑媒资", description = "课程计划解绑媒资")
    public void unassociateMedia(@PathVariable Long teachPlanId, @PathVariable String mediaId) {
        teachplanService.unassociateMedia(teachPlanId, mediaId);
    }
}

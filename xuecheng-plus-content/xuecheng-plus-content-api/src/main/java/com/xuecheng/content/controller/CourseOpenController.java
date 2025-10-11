package com.xuecheng.content.controller;

import com.xuecheng.content.model.dto.CoursePreviewDTO;
import com.xuecheng.content.service.CoursePublishService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/open")
@RequiredArgsConstructor
public class CourseOpenController {

    private final CoursePublishService coursePublishService;

    @GetMapping("/course/whole/{courseId}")
    public CoursePreviewDTO getCoursePreview(@PathVariable("courseId") Long courseId) {
        return coursePublishService.getCoursePreview(courseId);
    }
}

package com.xuecheng.content.controller;

import com.xuecheng.content.model.dto.CoursePreviewDTO;
import com.xuecheng.content.service.CoursePublishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class CoursePublishController {

    private final CoursePublishService coursePublishService;

    @GetMapping("/coursepreview/{courseId}")
    public ModelAndView preview(@PathVariable("courseId") Long courseId) {
        CoursePreviewDTO coursePreviewDTO = coursePublishService.getCoursePreview(courseId);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("course_template");
        modelAndView.addObject("model", coursePreviewDTO);
        return modelAndView;
    }

    @ResponseBody
    @PostMapping("/courseaudit/commit/{courseId}")
    public void commitAudit(@PathVariable("courseId") Long courseId) {
        // TODO 获取 companyId
        Long companyId = 1232141425L;
        coursePublishService.commitAudit(companyId, courseId);
    }
}

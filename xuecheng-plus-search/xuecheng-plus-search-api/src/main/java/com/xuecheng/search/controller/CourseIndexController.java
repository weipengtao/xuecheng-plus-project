package com.xuecheng.search.controller;

import com.xuecheng.search.model.po.CourseDoc;
import com.xuecheng.search.service.CourseIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/index")
@RequiredArgsConstructor
public class CourseIndexController {

    private final CourseIndexService courseIndexService;

    @PostMapping("/course")
    public Boolean addCourseDoc(CourseDoc courseDoc){
        return courseIndexService.indexDocument(courseDoc);
    }
}

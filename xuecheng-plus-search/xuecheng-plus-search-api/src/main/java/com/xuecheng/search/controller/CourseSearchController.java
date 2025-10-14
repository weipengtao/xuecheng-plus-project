package com.xuecheng.search.controller;

import com.xuecheng.search.model.dto.CourseSearchParamDTO;
import com.xuecheng.search.model.dto.CourseSearchResultDTO;
import com.xuecheng.search.model.po.CourseDoc;
import com.xuecheng.search.service.CourseSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseSearchController {

    private final CourseSearchService courseSearchService;

    @GetMapping("/list")
    public CourseSearchResultDTO<CourseDoc> list(CourseSearchParamDTO courseSearchParamDTO){
        return courseSearchService.list(courseSearchParamDTO);
    }
}

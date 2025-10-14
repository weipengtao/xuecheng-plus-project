package com.xuecheng.search.service;

import com.xuecheng.search.model.dto.CourseSearchParamDTO;
import com.xuecheng.search.model.dto.CourseSearchResultDTO;
import com.xuecheng.search.model.po.CourseDoc;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled
class CourseSearchServiceTest {

    @Autowired
    CourseSearchService courseSearchService;

    @Test
    void list() {
        CourseSearchParamDTO courseSearchParamDTO = new CourseSearchParamDTO();
        courseSearchParamDTO.setKeywords("Springboot");
        courseSearchParamDTO.setMt("编程开发");
        CourseSearchResultDTO<CourseDoc> list = courseSearchService.list(courseSearchParamDTO);
        list.getItems().forEach(item -> System.out.println(item.toString()));
        list.getMtList().forEach(System.out::println);
    }
}
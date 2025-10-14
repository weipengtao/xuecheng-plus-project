package com.xuecheng.content.clients;

import com.xuecheng.search.model.po.CourseDoc;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "search-service", path = "/search")
public interface SearchServiceClient {

    @PostMapping("/course")
    Boolean addCourseDoc(CourseDoc courseDoc);
}

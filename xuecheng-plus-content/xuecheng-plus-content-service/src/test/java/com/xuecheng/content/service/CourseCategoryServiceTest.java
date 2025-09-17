package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseCategoryTreeDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class CourseCategoryServiceTest {

    @Autowired
    private CourseCategoryService courseCategoryService;

    @Test
    void testGetTreeNodes() {
        List<CourseCategoryTreeDTO> tree = courseCategoryService.getTreeNodes("1");
        Assertions.assertNotNull(tree);
    }
}
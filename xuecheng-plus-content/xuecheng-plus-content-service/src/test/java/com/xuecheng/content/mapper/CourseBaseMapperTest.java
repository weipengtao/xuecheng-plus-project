package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.content.model.po.CourseBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CourseBaseMapperTest {

    @Autowired
    private CourseBaseMapper courseBaseMapper;

    @Test
    public void test() {
        CourseBase courseBase = courseBaseMapper.selectById(1);
        System.out.println(courseBase);
    }

    @Test
    public void testPagelist() {
        // 分页查询 查第2页 每页3条 mybatis-plus
        Page<CourseBase> page = new Page<>(2, 3);
        Page<CourseBase> courseBasePage = courseBaseMapper.selectPage(page, null);
        System.out.println(courseBasePage.getRecords());
    }
}
package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseTeacherServiceImpl implements CourseTeacherService {

    private final CourseTeacherMapper courseTeacherMapper;

    @Override
    public List<CourseTeacher> listByCourseId(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId);
        return courseTeacherMapper.selectList(queryWrapper);
    }

    @Override
    public CourseTeacher save(CourseTeacher courseTeacher) {
        courseTeacherMapper.insertOrUpdate(courseTeacher);
        return courseTeacher;
    }

    @Override
    public void delete(Long courseId, Long id) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId).eq(CourseTeacher::getId, id);
        courseTeacherMapper.delete(queryWrapper);
    }
}

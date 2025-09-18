package com.xuecheng.content.service;

import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

public interface CourseTeacherService {
    List<CourseTeacher> listByCourseId(Long courseId);

    CourseTeacher save(CourseTeacher courseTeacher);

    void delete(Long courseId, Long id);
}

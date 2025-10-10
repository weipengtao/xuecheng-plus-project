package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDTO;

public interface CoursePublishService {
    CoursePreviewDTO getCoursePreview(Long courseId);
}

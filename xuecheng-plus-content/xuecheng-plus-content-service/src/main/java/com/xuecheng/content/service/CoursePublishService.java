package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDTO;
import com.xuecheng.content.model.po.MqMessage;

public interface CoursePublishService {
    CoursePreviewDTO getCoursePreview(Long courseId);

    void commitAudit(Long companyId, Long courseId);

    void coursePublish(Long companyId, Long courseId);

    void processCoursePublishTask(MqMessage mqMessage);
}

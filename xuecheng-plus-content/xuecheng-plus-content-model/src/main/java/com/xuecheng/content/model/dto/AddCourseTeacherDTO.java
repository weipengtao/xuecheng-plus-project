package com.xuecheng.content.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "添加课程教师信息")
public class AddCourseTeacherDTO {
    /**
     * 课程id
     */
    private Long courseId;

    /**
     * 教师名称
     */
    private String teacherName;

    /**
     * 职务
     */
    private String position;

    /**
     * 描述
     */
    private String introduction;
}

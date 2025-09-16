package com.xuecheng.content.model.dto;

import lombok.Data;

@Data
public class QueryCourseParamsDTO {
    /**
     * 审核状态
     */
    private String auditStatus;
    /**
     * 发布状态
     */
    private String publishStatus;
    /**
     * 课程名称
     */
    private String courseName;
}

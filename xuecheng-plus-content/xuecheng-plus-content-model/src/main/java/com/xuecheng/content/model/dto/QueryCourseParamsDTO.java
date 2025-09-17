package com.xuecheng.content.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "课程查询参数")
public class QueryCourseParamsDTO {

    @Schema(description = "审核状态", example = "202004")
    private String auditStatus;

    @Schema(description = "发布状态", example = "203001")
    private String publishStatus;

    @Schema(description = "课程名称", example = "Java")
    private String courseName;
}

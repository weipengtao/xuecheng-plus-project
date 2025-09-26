package com.xuecheng.content.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "课程计划绑定媒资信息")
public class AssociateMediaDTO {
    /**
     * 媒资文件ID
     */
    @Schema(description = "媒资文件ID", example = "123456")
    private String mediaId;

    /**
     * 媒资文件名称
     */
    @Schema(description = "媒资文件名称", example = "example_video.mp4")
    private String fileName;

    /**
     * 课程计划ID
     */
    @Schema(description = "课程计划ID", example = "7890")
    private Long teachplanId;
}

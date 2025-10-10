package com.xuecheng.content.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(description = "课程预览信息")
public class CoursePreviewDTO {

    CourseBaseInfoDTO courseBase;

    List<TeachplanTreeNodeDTO> teachplans;
}

package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseBase;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class CourseBaseInfoDTO extends CourseBase {
    @Schema(description = "大分类名称")
    private String mtName;

    @Schema(description = "小分类名称")
    private String stName;

    @Schema(description = "课程发布id")
    private Long coursePubId;

    @Schema(description = "课程发布时间")
    private LocalDateTime coursePubDate;

    @Schema(description = "收费规则")
    private String charge;

    @Schema(description = "现价")
    private Double price;

    @Schema(description = "原价")
    private Double originalPrice;

    @Schema(description = "咨询QQ")
    private String qq;

    @Schema(description = "微信")
    private String wechat;

    @Schema(description = "电话")
    private String phone;

    @Schema(description = "有效期")
    private Integer validDays;
}

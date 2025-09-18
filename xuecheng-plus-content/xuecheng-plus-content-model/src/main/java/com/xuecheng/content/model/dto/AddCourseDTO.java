package com.xuecheng.content.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "新增课程信息")
public class AddCourseDTO {

    @Schema(description = "大分类", example = "1-1")
    private String mt;

    @Schema(description = "小分类", example = "1-1-2")
    private String st;

    @Schema(description = "课程名称", example = "Java编程基础")
    private String name;

    @Schema(description = "课程封面")
    private String pic;

    @Schema(description = "教学模式", example = "201001")
    private String teachmode;

    @Schema(description = "适用人群", example = "在校学生")
    private String users;

    @Schema(description = "课程标签", example = "Java,编程")
    private String tags;

    @Schema(description = "课程等级", example = "204002")
    private String grade;

    @Schema(description = "课程说明", example = "本课程主要讲解Java编程基础知识")
    private String description;

    @Schema(description = "收费规则", example = "201001")
    private String charge;

    @Schema(description = "现价", example = "99.00")
    private Double price;

    @Schema(description = "原价", example = "199.00")
    private Double originalPrice;

    @Schema(description = "咨询QQ", example = "123456789")
    private String qq;

    @Schema(description = "微信", example = "wxid_123456789")
    private String wechat;

    @Schema(description = "电话", example = "13800138000")
    private String phone;

    @Schema(description = "有效日期", example = "365")
    private Integer validDays;
}

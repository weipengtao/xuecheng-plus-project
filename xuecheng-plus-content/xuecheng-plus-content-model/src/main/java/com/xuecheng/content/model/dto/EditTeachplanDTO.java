package com.xuecheng.content.model.dto;

import lombok.Data;

@Data
public class EditTeachplanDTO {

    /***
     * 教学计划id
     */
    private Long id;

    /**
     * 课程计划名称
     */
    private String pname;

    /**
     * 课程计划父级Id
     */
    private Long parentid;

    /**
     * 层级，分为1、2、3级
     */
    private Short grade;

    /**
     * 课程标识
     */
    private Long courseId;
}

package com.xuecheng.search.model.dto;

import com.xuecheng.base.model.PageParams;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CourseSearchParamDTO extends PageParams {
    /**
     * 搜索关键字
     */
    private String keywords;
    /**
     * 大分类
     */
    private String mt;
    /**
     * 小分类
     */
    private String st;
    /**
     * 难度等级
     */
    private String grade;
}

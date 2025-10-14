package com.xuecheng.search.model.dto;

import com.xuecheng.base.model.PageResult;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseSearchResultDTO<T> extends PageResult<T> {
    /**
     * 大分类列表
     */
    private List<String> mtList;
    /**
     * 小分类列表
     */
    private List<String> stList;

    public CourseSearchResultDTO(List<T> items, Long counts, Long pageNo, Long pageSize, List<String> mtList, List<String> stList) {
        super(items, counts, pageNo, pageSize);
        this.mtList = mtList;
        this.stList = stList;
    }
}

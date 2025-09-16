package com.xuecheng.base.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {
    @Schema(description = "数据列表")
    private List<T> list;

    @Schema(description = "总记录数", example = "100")
    private Long counts;

    @Schema(description = "当前页码", example = "1")
    private Long page;

    @Schema(description = "每页记录数", example = "10")
    private Long pageSize;
}

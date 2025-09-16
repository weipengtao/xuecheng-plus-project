package com.xuecheng.base.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PageParams {
    /**
     * 当前页码
     */
    @Schema(description = "当前页码", example = "1")
    private Long pageNo = 1L;
    /**
     * 每页记录数
     */
    @Schema(description = "每页记录数", example = "10")
    private Long pageSize = 10L;
}

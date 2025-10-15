package com.xuecheng.auth.model.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author weipengtao
 * @since 2025-10-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;

    private String roleId;

    private String menuId;

    private LocalDateTime createTime;
}

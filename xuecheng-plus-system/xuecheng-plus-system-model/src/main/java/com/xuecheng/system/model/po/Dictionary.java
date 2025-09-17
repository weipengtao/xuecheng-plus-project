package com.xuecheng.system.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 数据字典
 * </p>
 *
 * @author weipengtao
 * @since 2025-09-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dictionary implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 数据字典名称
     */
    private String name;

    /**
     * 数据字典代码
     */
    private String code;

    /**
     * 数据字典项--json格式
  
     */
    private String itemValues;
}

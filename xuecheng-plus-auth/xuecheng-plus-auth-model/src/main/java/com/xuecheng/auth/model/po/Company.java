package com.xuecheng.auth.model.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

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
public class Company implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 联系人名称
     */
    private String linkname;

    /**
     * 名称
     */
    private String name;

    private String mobile;

    private String email;

    /**
     * 简介
     */
    private String intro;

    /**
     * logo
     */
    private String logo;

    /**
     * 身份证照片
     */
    private String identitypic;

    /**
     * 工具性质
     */
    private String worktype;

    /**
     * 营业执照
     */
    private String businesspic;

    /**
     * 企业状态
     */
    private String status;
}

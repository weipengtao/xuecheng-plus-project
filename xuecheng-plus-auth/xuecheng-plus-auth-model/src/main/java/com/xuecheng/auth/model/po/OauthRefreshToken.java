package com.xuecheng.auth.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("oauth_refresh_token")
public class OauthRefreshToken implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String tokenId;

    private byte[] token;

    private byte[] authentication;
}

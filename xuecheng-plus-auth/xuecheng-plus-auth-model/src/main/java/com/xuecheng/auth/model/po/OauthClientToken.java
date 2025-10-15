package com.xuecheng.auth.model.po;

import com.baomidou.mybatisplus.annotation.TableId;
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
@TableName("oauth_client_token")
public class OauthClientToken implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String tokenId;

    private byte[] token;

    @TableId("authentication_id")
    private String authenticationId;

    private String userName;

    private String clientId;
}

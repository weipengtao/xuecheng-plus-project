package com.xuecheng.auth.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("oauth_approvals")
public class OauthApprovals implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String userId;

    private String clientId;

    private String scope;

    private String status;

    private LocalDateTime expiresAt;

    private LocalDateTime lastModifiedAt;
}

package com.xuecheng.auth.service.impl;

import com.xuecheng.auth.model.po.OauthAccessToken;
import com.xuecheng.auth.mapper.OauthAccessTokenMapper;
import com.xuecheng.auth.service.IOauthAccessTokenService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author weipengtao
 * @since 2025-10-15
 */
@Service
public class OauthAccessTokenServiceImpl extends ServiceImpl<OauthAccessTokenMapper, OauthAccessToken> implements IOauthAccessTokenService {

}

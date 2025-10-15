package com.xuecheng.auth.service.impl;

import com.xuecheng.auth.model.po.OauthCode;
import com.xuecheng.auth.mapper.OauthCodeMapper;
import com.xuecheng.auth.service.IOauthCodeService;
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
public class OauthCodeServiceImpl extends ServiceImpl<OauthCodeMapper, OauthCode> implements IOauthCodeService {

}

package com.xuecheng.auth.service.impl;

import com.xuecheng.auth.model.po.User;
import com.xuecheng.auth.mapper.UserMapper;
import com.xuecheng.auth.service.IUserService;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}

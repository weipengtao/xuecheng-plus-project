package com.xuecheng.auth.service.impl;

import com.xuecheng.auth.model.po.Permission;
import com.xuecheng.auth.mapper.PermissionMapper;
import com.xuecheng.auth.service.IPermissionService;
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
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements IPermissionService {

}

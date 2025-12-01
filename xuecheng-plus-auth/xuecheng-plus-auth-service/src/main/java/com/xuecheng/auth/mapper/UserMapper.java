package com.xuecheng.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.auth.model.po.User;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author weipengtao
 * @since 2025-10-15
 */
public interface UserMapper extends BaseMapper<User> {

    List<String> getAuthoritiesByUserId(String userId);
}

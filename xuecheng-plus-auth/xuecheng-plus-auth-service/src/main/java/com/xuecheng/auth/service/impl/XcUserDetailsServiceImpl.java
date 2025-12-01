package com.xuecheng.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.auth.mapper.UserMapper;
import com.xuecheng.auth.model.po.SecurityUser;
import com.xuecheng.auth.model.po.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class XcUserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        List<String> permNames = userMapper.getAuthoritiesByUserId(user.getId());

        Set<SimpleGrantedAuthority> authorities = permNames == null ? Set.of() : permNames.stream()
                .filter(p -> p != null && !p.isBlank())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return SecurityUser.builder()
                .user(user)
                .authorities(authorities)
                .build();
    }
}

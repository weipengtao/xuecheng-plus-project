package com.xuecheng.auth.controller;

import com.xuecheng.auth.model.po.User;
import com.xuecheng.auth.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final IUserService userService;

    @RequestMapping("/login-success")
    public String loginSuccess() {
        return "登录成功";
    }


    @RequestMapping("/user/{id}")
    public User getuser(@PathVariable("id") String id) {
        return userService.getById(id);
    }

    @RequestMapping("/r/r1")
    @PreAuthorize("hasAuthority('p1')")
    public String r1() {
        return "访问r1资源";
    }

    @RequestMapping("/r/r2")
    @PreAuthorize("hasAuthority('p2')")
    public String r2() {
        return "访问r2资源";
    }
}

package com.xuecheng.gateway.controller;

import com.xuecheng.gateway.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @RequestMapping("/authorization-code/login/callback")
    public Mono<Void> loginCallback(ServerWebExchange exchange, String code) {
        log.debug("登录成功回调，授权码：{}", code);

        // 申请令牌
        Map<String, Object> map = loginService.applyToken(code);
        String accessToken = (String) map.get("access_token");

        // 放到 cookie
        // ==== 将 access_token 放入 Cookie ====
        if (accessToken != null) {
            ResponseCookie cookie = ResponseCookie.from("jwt", accessToken)
                    .httpOnly(false) // JS 可读
                    .path("/")      // 全局有效
                    .maxAge(60 * 60) // 1小时过期
                    .sameSite("Lax") // 防止跨站攻击
                    .build();
            exchange.getResponse().addCookie(cookie);
        }

        // 设置重定向地址
        exchange.getResponse().setStatusCode(HttpStatus.FOUND);
        exchange.getResponse().getHeaders().setLocation(URI.create("http://www.51xuecheng.cn"));

        // 返回完成的Mono，表示请求处理完毕
        return exchange.getResponse().setComplete();
    }
}

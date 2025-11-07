package com.xuecheng.gateway.service.impl;

import com.xuecheng.gateway.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    // 授权服务器令牌端点
    private static final String TOKEN_URL = "http://localhost:8020/auth/oauth2/token";

    // 客户端凭证
    private static final String CLIENT_ID = "oidc-client";
    private static final String CLIENT_SECRET = "secret";

    // 回调地址（需与授权服务器注册的 redirect_uri 一致）
    private static final String REDIRECT_URI = "http://www.51xuecheng.cn/api/authorization-code/login/callback";

    @Override
    public Map<String, Object> applyToken(String code) {
        try {
            // 使用 RestTemplate 发送 POST 请求
            RestTemplate restTemplate = new RestTemplate();

            // 构造请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // 生成 Basic Authorization 头
            String auth = CLIENT_ID + ":" + CLIENT_SECRET;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
            headers.set("Authorization", "Basic " + encodedAuth);

            // 构造请求体参数
            String body = "grant_type=authorization_code"
                    + "&code=" + code
                    + "&redirect_uri=" + REDIRECT_URI;

            HttpEntity<String> request = new HttpEntity<>(body, headers);

            // 发送请求
            ResponseEntity<Map> response = restTemplate.exchange(
                    TOKEN_URL,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            Map<String, Object> tokenResponse = response.getBody();
            log.info("获取到访问令牌响应: {}", tokenResponse);
            return tokenResponse != null ? tokenResponse : Map.of("error", "empty response");

        } catch (Exception e) {
            log.error("申请访问令牌失败", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "token_request_failed");
            error.put("message", e.getMessage());
            return error;
        }
    }
}

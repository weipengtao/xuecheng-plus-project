package com.xuecheng.captcha.controller;

import com.xuecheng.captcha.model.po.CaptchaDTO;
import com.xuecheng.captcha.service.CaptchaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CaptchaController {

    private final CaptchaService captchaService;

    @PostMapping("/image")
    public CaptchaDTO generateImageCode() {
        return captchaService.generate();
    }

    @PostMapping("/verify")
    public Boolean verify(String key, String code) {
        return captchaService.verify(key, code);
    }
}

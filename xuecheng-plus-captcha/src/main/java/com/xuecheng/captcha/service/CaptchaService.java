package com.xuecheng.captcha.service;

import com.xuecheng.captcha.model.po.CaptchaDTO;

public interface CaptchaService {
    /**
     * 生成验证码（可以是图形、短信、邮件等）
     * @return CaptchaDTO 包含 key 和混淆内容（如图形Base64）
     */
    CaptchaDTO generate();

    /**
     * 校验验证码
     * @param key 验证码 key
     * @param code 用户输入
     * @return 是否正确
     */
    boolean verify(String key, String code);
}

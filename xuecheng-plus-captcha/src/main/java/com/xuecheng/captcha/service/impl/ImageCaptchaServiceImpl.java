package com.xuecheng.captcha.service.impl;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.xuecheng.captcha.model.po.CaptchaDTO;
import com.xuecheng.captcha.service.CaptchaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ImageCaptchaServiceImpl implements CaptchaService {

    private final DefaultKaptcha kaptcha;
    private final StringRedisTemplate redisTemplate;

    private static final String REDIS_KEY_PREFIX = "captcha:image:";

    @Override
    public CaptchaDTO generate() {
        // 生成验证码文本
        String codeText = kaptcha.createText();

        // 生成图片
        BufferedImage image = kaptcha.createImage(codeText);

        // 生成唯一 key
        String key = UUID.randomUUID().toString();

        // 保存到 Redis 5 分钟
        redisTemplate.opsForValue().set(REDIS_KEY_PREFIX + key, codeText, 5, TimeUnit.MINUTES);

        // 转 Base64
        String base64Image;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", baos);
            base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("生成验证码图片失败", e);
        }

        // 封装返回
        CaptchaDTO dto = new CaptchaDTO();
        dto.setKey(key);
        dto.setAliasing("data:image/jpeg;base64," + base64Image);
        return dto;
    }

    @Override
    public boolean verify(String key, String code) {
        String redisKey = REDIS_KEY_PREFIX + key;
        String value = redisTemplate.opsForValue().get(redisKey);
        if (value != null && value.equalsIgnoreCase(code)) {
            redisTemplate.delete(redisKey); // 校验后删除
            return true;
        }
        return false;
    }
}

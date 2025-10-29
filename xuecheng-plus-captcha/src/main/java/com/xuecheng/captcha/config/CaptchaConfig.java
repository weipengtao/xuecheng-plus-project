package com.xuecheng.captcha.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class CaptchaConfig {

    @Bean
    public DefaultKaptcha kaptchaProducer() {
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Properties props = new Properties();
        props.put("kaptcha.border", "no");
        props.put("kaptcha.textproducer.font.color", "black");
        props.put("kaptcha.textproducer.char.space", "10");
        props.put("kaptcha.textproducer.char.length","4");
        props.put("kaptcha.image.height","34");
        props.put("kaptcha.image.width","138");
        props.put("kaptcha.textproducer.font.size","25");

        props.put("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise");
        kaptcha.setConfig(new Config(props));
        return kaptcha;
    }
}

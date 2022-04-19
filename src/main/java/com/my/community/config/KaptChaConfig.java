package com.my.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptChaConfig {
    @Bean
    public Producer kaptChaProducer() {
        Properties properties = new Properties();
        //图片边框
        properties.put("kaptcha.border", "no");
        properties.setProperty("kaptcha.image.width", "100");
        properties.setProperty("kaptcha.image.height", "40");
        //字体大小
        properties.setProperty("kaptcha.textproducer.font.size", "32");
        //字体颜色
        properties.setProperty("kaptcha.textproducer.font.color", "0,0,0");
        //验证码长度
//        properties.put("kaptcha.textproducer.char.length", "5");
        //字体
        properties.setProperty("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");
        properties.setProperty("kaptcha.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        properties.setProperty("kaptcha.char.length", "4");
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
        DefaultKaptcha kaptCha = new DefaultKaptcha();
        Config config = new Config(properties);
        kaptCha.setConfig(config);
        return kaptCha;
    }
}

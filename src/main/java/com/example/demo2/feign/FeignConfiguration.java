package com.example.demo2.feign;

import feign.RequestInterceptor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RefreshScope
@ConfigurationProperties(prefix = "custom")
@Configuration
public class FeignConfiguration {

    private String cookie;

    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor(){
        return template -> {
            template.header("cookie",cookie);
        };
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }
}

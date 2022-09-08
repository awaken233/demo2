package com.example.demo2.feign;

import com.example.demo2.config.CustomProperties;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoProviderFeignClientConfiguration {

    @Autowired
    private CustomProperties customProperties;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return tpl -> {
            tpl.header("cookie", customProperties.getCookie());
        };
    }
}
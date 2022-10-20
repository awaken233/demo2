package com.example.demo2.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * https://cloud.spring.io/spring-cloud-openfeign/reference/html/#spring-cloud-feign
 */
@Configuration
public class FeignConfiguration {

    @Autowired
    private CustomProperties customProperties;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return tpl -> {
            tpl.header("cookie", customProperties.getCookie());
        };
    }

}
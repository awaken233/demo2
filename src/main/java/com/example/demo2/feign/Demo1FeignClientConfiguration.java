package com.example.demo2.feign;

import com.example.demo2.config.CustomProperties;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

/**
 * https://cloud.spring.io/spring-cloud-openfeign/reference/html/#spring-cloud-feign
 */
public class Demo1FeignClientConfiguration {

    @Autowired
    private CustomProperties customProperties;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return tpl -> {
            tpl.header("cookie", customProperties.getCookie());
        };
    }
}
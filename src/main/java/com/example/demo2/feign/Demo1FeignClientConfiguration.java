package com.example.demo2.feign;

import com.example.demo2.config.CustomProperties;
import feign.Logger;
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

    @Bean
    Logger.Level feignLoggerLevel() {
        //这里记录所有，根据实际情况选择合适的日志level，此处取值共有四个NONE、BASIC、HEADERS和FULL
        return Logger.Level.HEADERS;
    }

}
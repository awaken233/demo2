package com.example.demo2.config;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * https://cloud.spring.io/spring-cloud-openfeign/reference/html/#spring-cloud-feign
 */
@Slf4j
@Configuration
public class FeignConfiguration {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return tpl -> {
        };
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response)->{
            log.info("methodKey: {}, response {}", methodKey, response);
            throw new RuntimeException("error decoder");
        };
    }
}
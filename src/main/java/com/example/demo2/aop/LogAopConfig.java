package com.example.demo2.aop;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wlei3
 * @since 2021/11/22 15:24
 */
@Configuration
public class LogAopConfig {

    @Bean
    public LogAop logAop() {
        return new LogAop();
    }
}

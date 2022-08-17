package com.example.demo2.aop;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguretion implements WebMvcConfigurer {

    /**
     * 添加拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getClearHandlerInterceptor())
            .addPathPatterns("/**");
    }

    @Bean
    public ClearHandlerInterceptor getClearHandlerInterceptor() {
        return new ClearHandlerInterceptor();
    }

}
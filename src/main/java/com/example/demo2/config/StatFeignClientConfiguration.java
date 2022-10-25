package com.example.demo2.config;

import com.example.demo2.component.StatFeignClientBeanProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnClass(name = "feign.Client")
public class StatFeignClientConfiguration {

    @Bean
    public StatFeignClientBeanProcessor rpcMetricsExecutionProcessor() {
        return new StatFeignClientBeanProcessor();
    }

}
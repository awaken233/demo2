package com.example.demo2.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RocketMQConfig {

    /**
     * 定义RocketMQ的Topic常量
     */
    public static final String TEST_TOPIC = "test_topic";
    
    /**
     * 定义消费者组常量
     */
    public static final String TEST_CONSUMER_GROUP = "test_consumer_group";
} 
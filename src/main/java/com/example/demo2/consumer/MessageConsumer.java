package com.example.demo2.consumer;

import com.example.demo2.config.RocketMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = RocketMQConfig.TEST_TOPIC,
        consumerGroup = RocketMQConfig.TEST_CONSUMER_GROUP
)
public class MessageConsumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        log.info("消费者接收到消息: {}", message);
        // 这里处理接收到的消息
        log.info("消息处理完成");
    }
} 
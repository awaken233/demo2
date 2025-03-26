package com.example.demo2.producer;

import com.example.demo2.config.RocketMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageProducer {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 发送同步消息
     * @param message 消息内容
     * @return 发送结果
     */
    public SendResult sendMessage(String message) {
        log.info("准备发送消息到topic: {}, 消息内容: {}", RocketMQConfig.TEST_TOPIC, message);
        SendResult sendResult = rocketMQTemplate.syncSend(RocketMQConfig.TEST_TOPIC, 
                                                MessageBuilder.withPayload(message).build());
        log.info("消息发送成功，结果: {}", sendResult);
        return sendResult;
    }
    
    /**
     * 发送异步消息
     * @param message 消息内容
     */
    public void sendAsyncMessage(String message) {
        log.info("准备异步发送消息到topic: {}, 消息内容: {}", RocketMQConfig.TEST_TOPIC, message);
        rocketMQTemplate.asyncSend(RocketMQConfig.TEST_TOPIC, 
                               MessageBuilder.withPayload(message).build(), 
                               null);
        log.info("异步消息发送完成");
    }
} 
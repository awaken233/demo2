package com.example.demo2.controller;

import com.example.demo2.producer.MessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageProducer messageProducer;

    /**
     * 发送同步消息
     */
    @GetMapping("/send")
    public Map<String, Object> sendMessage(@RequestParam(value = "content", defaultValue = "默认消息") String content) {
        Map<String, Object> result = new HashMap<>();
        try {
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String message = content + " - 发送时间: " + time;
            
            SendResult sendResult = messageProducer.sendMessage(message);
            
            result.put("success", true);
            result.put("message", "消息发送成功");
            result.put("data", sendResult);
        } catch (Exception e) {
            log.error("消息发送失败", e);
            result.put("success", false);
            result.put("message", "消息发送失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 发送异步消息
     */
    @GetMapping("/send-async")
    public Map<String, Object> sendAsyncMessage(@RequestParam(value = "content", defaultValue = "默认异步消息") String content) {
        Map<String, Object> result = new HashMap<>();
        try {
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String message = content + " - 异步发送时间: " + time;
            
            messageProducer.sendAsyncMessage(message);
            
            result.put("success", true);
            result.put("message", "异步消息发送成功");
        } catch (Exception e) {
            log.error("异步消息发送失败", e);
            result.put("success", false);
            result.put("message", "异步消息发送失败: " + e.getMessage());
        }
        return result;
    }
} 
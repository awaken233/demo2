package com.example.demo2.controller;

import com.example.demo2.service.RedisTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

/**
 * @author wlei3
 * @since 2022/10/20 20:13
 */
@Slf4j
@RestController
public class TestController {

    @Autowired
    private RedisTransactionService redisTransactionService;

    /**
     * 测试Redis事务回滚
     */
    @PostMapping("/redis/tx/rollback")
    public Map<String, Object> testRedisTransactionRollback() {
        log.info("测试Redis事务回滚");
        try {
            return redisTransactionService.testTransactionRollback();
        } catch (Exception e) {
            log.error("Redis事务回滚测试失败", e);
            return Collections.singletonMap("error", e.getMessage());
        }
    }
}

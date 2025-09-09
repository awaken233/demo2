package com.example.demo2.controller;

import com.example.demo2.service.RedisTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    // ==================== Redis事务测试接口 ====================

    /**
     * 测试Redis事务成功提交
     */
    @PostMapping("/redis/tx/success")
    public Map<String, Object> testRedisTransactionSuccess() {
        log.info("测试Redis事务成功提交");
        try {
            return redisTransactionService.testTransactionSuccess();
        } catch (Exception e) {
            log.error("Redis事务成功测试失败", e);
            return Collections.singletonMap("error", e.getMessage());
        }
    }

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

    /**
     * 测试Redis事务条件回滚
     */
    @PostMapping("/redis/tx/conditional")
    public Map<String, Object> testRedisTransactionConditional(@RequestParam(defaultValue = "false") boolean shouldRollback) {
        log.info("测试Redis事务条件回滚，shouldRollback: {}", shouldRollback);
        try {
            return redisTransactionService.testTransactionConditionalRollback(shouldRollback);
        } catch (Exception e) {
            log.error("Redis事务条件回滚测试失败", e);
            return Collections.singletonMap("error", e.getMessage());
        }
    }

    /**
     * 测试StringRedisTemplate事务
     */
    @PostMapping("/redis/tx/string")
    public Map<String, Object> testStringRedisTemplateTransaction() {
        log.info("测试StringRedisTemplate事务");
        try {
            return redisTransactionService.testStringRedisTemplateTransaction();
        } catch (Exception e) {
            log.error("StringRedisTemplate事务测试失败", e);
            return Collections.singletonMap("error", e.getMessage());
        }
    }

    /**
     * 检查Redis中的数据
     */
    @GetMapping("/redis/data")
    public Map<String, Object> checkRedisData() {
        log.info("检查Redis中的数据");
        return redisTransactionService.checkRedisData();
    }

    /**
     * 清理测试数据
     */
    @DeleteMapping("/redis/cleanup")
    public Map<String, Object> cleanupTestData() {
        log.info("清理Redis测试数据");
        redisTransactionService.cleanupTestData();
        return Collections.singletonMap("message", "测试数据清理完成");
    }
}

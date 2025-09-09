package com.example.demo2.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Redis事务测试服务
 */
@Slf4j
@Service
public class RedisTransactionService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 测试Redis事务成功提交
     */
    @Transactional
    public Map<String, Object> testTransactionSuccess() {
        log.info("开始执行Redis事务 - 成功场景");
        
        try {
            // 执行多个Redis操作
            redisTemplate.opsForValue().set("tx:key1", "value1");
            redisTemplate.opsForValue().set("tx:key2", "value2");
            redisTemplate.opsForHash().put("tx:hash", "field1", "hashValue1");
            redisTemplate.opsForHash().put("tx:hash", "field2", "hashValue2");
            
            log.info("Redis事务操作完成，准备提交");
            
            Map<String, Object> result = new HashMap<>();
            result.put("status", "success");
            result.put("message", "事务成功提交");
            result.put("keys", redisTemplate.keys("tx:*"));
            
            return result;
        } catch (Exception e) {
            log.error("Redis事务执行异常", e);
            throw e;
        }
    }

    /**
     * 测试Redis事务回滚
     */
    @Transactional
    public Map<String, Object> testTransactionRollback() {
        log.info("开始执行Redis事务 - 回滚场景");
        
        try {
            // 执行一些Redis操作
            redisTemplate.opsForValue().set("tx:rollback:key1", "value1");
            redisTemplate.opsForValue().set("tx:rollback:key2", "value2");
            redisTemplate.opsForHash().put("tx:rollback:hash", "field1", "hashValue1");
            
            log.info("Redis操作完成，准备抛出异常触发回滚");
            
            // 故意抛出异常来触发事务回滚
            throw new RuntimeException("模拟业务异常，触发事务回滚");
            
        } catch (Exception e) {
            log.error("Redis事务执行异常，将回滚", e);
            throw e;
        }
    }

    /**
     * 测试Redis事务 - 条件回滚
     */
    @Transactional
    public Map<String, Object> testTransactionConditionalRollback(boolean shouldRollback) {
        log.info("开始执行Redis事务 - 条件回滚场景，shouldRollback: {}", shouldRollback);
        
        try {
            // 执行Redis操作
            redisTemplate.opsForValue().set("tx:conditional:key1", "value1");
            redisTemplate.opsForValue().set("tx:conditional:key2", "value2");
            
            if (shouldRollback) {
                log.info("根据条件触发回滚");
                throw new RuntimeException("条件回滚：shouldRollback=true");
            }
            
            log.info("Redis事务操作完成，准备提交");
            
            Map<String, Object> result = new HashMap<>();
            result.put("status", "success");
            result.put("message", "事务成功提交");
            result.put("shouldRollback", shouldRollback);
            
            return result;
            
        } catch (Exception e) {
            log.error("Redis事务执行异常", e);
            throw e;
        }
    }

    /**
     * 测试StringRedisTemplate事务
     */
    @Transactional
    public Map<String, Object> testStringRedisTemplateTransaction() {
        log.info("开始执行StringRedisTemplate事务测试");
        
        try {
            // 使用StringRedisTemplate执行操作
            stringRedisTemplate.opsForValue().set("string:tx:key1", "stringValue1");
            stringRedisTemplate.opsForValue().set("string:tx:key2", "stringValue2");
            stringRedisTemplate.opsForList().leftPush("string:tx:list", "listItem1");
            stringRedisTemplate.opsForList().leftPush("string:tx:list", "listItem2");
            
            log.info("StringRedisTemplate事务操作完成");
            
            Map<String, Object> result = new HashMap<>();
            result.put("status", "success");
            result.put("message", "StringRedisTemplate事务成功提交");
            result.put("keys", stringRedisTemplate.keys("string:tx:*"));
            
            return result;
            
        } catch (Exception e) {
            log.error("StringRedisTemplate事务执行异常", e);
            throw e;
        }
    }

    /**
     * 检查Redis中的数据
     */
    public Map<String, Object> checkRedisData() {
        Map<String, Object> result = new HashMap<>();
        
        // 检查事务相关的key
        result.put("tx:keys", redisTemplate.keys("tx:*"));
        result.put("string:tx:keys", stringRedisTemplate.keys("string:tx:*"));
        
        // 检查具体的值
        result.put("tx:key1", redisTemplate.opsForValue().get("tx:key1"));
        result.put("tx:key2", redisTemplate.opsForValue().get("tx:key2"));
        result.put("tx:hash", redisTemplate.opsForHash().entries("tx:hash"));
        
        result.put("string:tx:key1", stringRedisTemplate.opsForValue().get("string:tx:key1"));
        result.put("string:tx:list", stringRedisTemplate.opsForList().range("string:tx:list", 0, -1));
        
        return result;
    }

    /**
     * 清理测试数据
     */
    public void cleanupTestData() {
        log.info("清理Redis测试数据");
        
        // 删除事务相关的key
        redisTemplate.delete(redisTemplate.keys("tx:*"));
        stringRedisTemplate.delete(stringRedisTemplate.keys("string:tx:*"));
        
        log.info("Redis测试数据清理完成");
    }
}

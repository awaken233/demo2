package com.example.demo2.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Map;

/**
 * Redis事务测试服务
 */
@Slf4j
@Service
public class RedisTransactionService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 测试Redis事务回滚
     */
    @Transactional
    public Map<String, Object> testTransactionRollback() {
        log.info("开始执行Redis事务 - 回滚场景");
        
        try {
            // 执行一些Redis操作
            redisTemplate.setEnableTransactionSupport(true);
            redisTemplate.multi();
            redisTemplate.opsForValue().set("tx:rollback:key1", "value1");
            redisTemplate.opsForValue().set("tx:rollback:key2", "value2");
            redisTemplate.opsForHash().put("tx:rollback:hash", "field1", "hashValue1");
            
            log.info("Redis操作完成，准备抛出异常触发回滚");
            
            // 故意抛出异常来触发事务回滚
            throw new RuntimeException("模拟业务异常，触发事务回滚");
            
        } catch (Exception e) {
            log.error("Redis事务执行异常，将回滚");
            redisTemplate.discard();
            throw e;
        } finally {
            TransactionSynchronizationManager.unbindResource(redisTemplate.getConnectionFactory());
        }
    }
}

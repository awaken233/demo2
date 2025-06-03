package com.example.demo2;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 线程池测试类
 * @author wlei3
 * @since 2025-06-03
 */
@Slf4j
@SpringBootTest
@SpringJUnitConfig
public class ThreadPoolTest {

    @Autowired
    @Qualifier("syncTestPool")
    private ThreadPoolTaskExecutor syncTestPool;

    @Autowired
    @Qualifier("asyncTestPool")
    private ThreadPoolTaskExecutor asyncTestPool;

    @Test
    public void testSyncThreadPoolConfiguration() {
        assertNotNull(syncTestPool);
        assertEquals(100, syncTestPool.getCorePoolSize());
        assertEquals(100, syncTestPool.getMaxPoolSize());
        assertTrue(syncTestPool.getThreadNamePrefix().startsWith("syncTest-"));
        log.info("同步线程池配置测试通过");
    }

    @Test
    public void testAsyncThreadPoolConfiguration() {
        assertNotNull(asyncTestPool);
        assertEquals(100, asyncTestPool.getCorePoolSize());
        assertEquals(100, asyncTestPool.getMaxPoolSize());
        assertTrue(asyncTestPool.getThreadNamePrefix().startsWith("asyncTest-"));
        log.info("异步线程池配置测试通过");
    }

    @Test
    public void testThreadPoolConcurrency() throws InterruptedException {
        int taskCount = 50;
        CountDownLatch latch = new CountDownLatch(taskCount * 2);

        // 测试同步线程池
        for (int i = 0; i < taskCount; i++) {
            final int taskId = i;
            syncTestPool.submit(() -> {
                try {
                    log.debug("同步任务 {} 开始执行，线程：{}", taskId, Thread.currentThread().getName());
                    TimeUnit.MILLISECONDS.sleep(50);
                    log.debug("同步任务 {} 完成，线程：{}", taskId, Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        // 测试异步线程池
        for (int i = 0; i < taskCount; i++) {
            final int taskId = i;
            asyncTestPool.submit(() -> {
                try {
                    log.debug("异步任务 {} 开始执行，线程：{}", taskId, Thread.currentThread().getName());
                    TimeUnit.MILLISECONDS.sleep(50);
                    log.debug("异步任务 {} 完成，线程：{}", taskId, Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        // 等待所有任务完成
        boolean completed = latch.await(10, TimeUnit.SECONDS);
        assertTrue(completed, "所有任务应该在10秒内完成");
        
        log.info("并发测试完成，同步线程池活跃线程数：{}，异步线程池活跃线程数：{}", 
                syncTestPool.getActiveCount(), asyncTestPool.getActiveCount());
    }
} 
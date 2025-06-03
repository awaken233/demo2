package com.example.demo2;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * FastAPI性能测试类
 * @author wlei3
 * @since 2025-06-03
 */
@Slf4j
@SpringBootTest
@SpringJUnitConfig
public class FastApiPerformanceTest {

    @Autowired
    @Qualifier("syncTestPool")
    private ThreadPoolTaskExecutor syncTestPool;

    @Autowired
    @Qualifier("asyncTestPool")
    private ThreadPoolTaskExecutor asyncTestPool;

    private static final String SYNC_API_URL = "http://192.168.16.13:10202/api/test/test";
    private static final String ASYNC_API_URL = "http://192.168.16.13:8787/index/queue";

    /**
     * 测试同步接口性能
     */
    public void testSyncApiPerformance() throws InterruptedException {
        int requestCount = 100;
        CountDownLatch latch = new CountDownLatch(requestCount);
        AtomicLong totalTime = new AtomicLong(0);
        AtomicLong successCount = new AtomicLong(0);
        List<Long> responseTimes = new ArrayList<>();

        log.info("开始测试同步接口性能，请求数：{}", requestCount);
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < requestCount; i++) {
            final int requestId = i;
            syncTestPool.submit(() -> {
                try {
                    long requestStartTime = System.currentTimeMillis();
                    
                    RestTemplate restTemplate = new RestTemplate();
                    ResponseEntity<String> response = restTemplate.getForEntity(SYNC_API_URL, String.class);
                    
                    long requestEndTime = System.currentTimeMillis();
                    long responseTime = requestEndTime - requestStartTime;
                    
                    synchronized (responseTimes) {
                        responseTimes.add(responseTime);
                    }
                    
                    totalTime.addAndGet(responseTime);
                    
                    if (response.getStatusCode().is2xxSuccessful()) {
                        successCount.incrementAndGet();
                        log.debug("同步请求 {} 成功，耗时：{}ms，状态码：{}", 
                                requestId, responseTime, response.getStatusCode());
                    } else {
                        log.warn("同步请求 {} 失败，状态码：{}", requestId, response.getStatusCode());
                    }
                    
                } catch (Exception e) {
                    log.error("同步请求 {} 异常：{}", requestId, e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(60, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();
        
        if (completed) {
            double avgResponseTime = (double) totalTime.get() / successCount.get();
            double totalTestTime = (endTime - startTime) / 1000.0;
            double qps = successCount.get() / totalTestTime;
            
            // 计算响应时间统计
            responseTimes.sort(Long::compareTo);
            long minTime = responseTimes.get(0);
            long maxTime = responseTimes.get(responseTimes.size() - 1);
            long p50Time = responseTimes.get((int) (responseTimes.size() * 0.5));
            long p90Time = responseTimes.get((int) (responseTimes.size() * 0.9));
            long p95Time = responseTimes.get((int) (responseTimes.size() * 0.95));
            
            log.info("同步接口测试完成！");
            log.info("总请求数：{}，成功数：{}，失败数：{}", 
                    requestCount, successCount.get(), requestCount - successCount.get());
            log.info("总耗时：{}秒", String.format("%.2f", totalTestTime));
            log.info("QPS：{}", String.format("%.2f", qps));
            log.info("平均响应时间：{}ms", String.format("%.2f", avgResponseTime));
            log.info("响应时间统计 - 最小：{}ms，最大：{}ms，P50：{}ms，P90：{}ms，P95：{}ms", 
                    minTime, maxTime, p50Time, p90Time, p95Time);
            log.info("线程池状态 - 活跃：{}，总数：{}，队列：{}", 
                    syncTestPool.getActiveCount(), 
                    syncTestPool.getPoolSize(),
                    syncTestPool.getThreadPoolExecutor().getQueue().size());
        } else {
            log.error("同步接口测试超时！");
        }
    }

    /**
     * 测试异步接口性能
     */
    public void testAsyncApiPerformance() throws InterruptedException {
        int requestCount = 100;
        CountDownLatch latch = new CountDownLatch(requestCount);
        AtomicLong totalTime = new AtomicLong(0);
        AtomicLong successCount = new AtomicLong(0);
        List<Long> responseTimes = new ArrayList<>();

        log.info("开始测试异步接口性能，请求数：{}", requestCount);
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < requestCount; i++) {
            final int requestId = i;
            asyncTestPool.submit(() -> {
                try {
                    long requestStartTime = System.currentTimeMillis();
                    
                    RestTemplate restTemplate = new RestTemplate();
                    ResponseEntity<String> response = restTemplate.getForEntity(ASYNC_API_URL, String.class);
                    
                    long requestEndTime = System.currentTimeMillis();
                    long responseTime = requestEndTime - requestStartTime;
                    
                    synchronized (responseTimes) {
                        responseTimes.add(responseTime);
                    }
                    
                    totalTime.addAndGet(responseTime);
                    
                    if (response.getStatusCode().is2xxSuccessful()) {
                        successCount.incrementAndGet();
                        log.debug("异步请求 {} 成功，耗时：{}ms，状态码：{}", 
                                requestId, responseTime, response.getStatusCode());
                    } else {
                        log.warn("异步请求 {} 失败，状态码：{}", requestId, response.getStatusCode());
                    }
                    
                } catch (Exception e) {
                    log.error("异步请求 {} 异常：{}", requestId, e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(60, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();
        
        if (completed) {
            double avgResponseTime = (double) totalTime.get() / successCount.get();
            double totalTestTime = (endTime - startTime) / 1000.0;
            double qps = successCount.get() / totalTestTime;
            
            // 计算响应时间统计
            responseTimes.sort(Long::compareTo);
            long minTime = responseTimes.get(0);
            long maxTime = responseTimes.get(responseTimes.size() - 1);
            long p50Time = responseTimes.get((int) (responseTimes.size() * 0.5));
            long p90Time = responseTimes.get((int) (responseTimes.size() * 0.9));
            long p95Time = responseTimes.get((int) (responseTimes.size() * 0.95));
            
            log.info("异步接口测试完成！");
            log.info("总请求数：{}，成功数：{}，失败数：{}", 
                    requestCount, successCount.get(), requestCount - successCount.get());
            log.info("总耗时：{}秒", String.format("%.2f", totalTestTime));
            log.info("QPS：{}", String.format("%.2f", qps));
            log.info("平均响应时间：{}ms", String.format("%.2f", avgResponseTime));
            log.info("响应时间统计 - 最小：{}ms，最大：{}ms，P50：{}ms，P90：{}ms，P95：{}ms", 
                    minTime, maxTime, p50Time, p90Time, p95Time);
            log.info("线程池状态 - 活跃：{}，总数：{}，队列：{}", 
                    asyncTestPool.getActiveCount(), 
                    asyncTestPool.getPoolSize(),
                    asyncTestPool.getThreadPoolExecutor().getQueue().size());
        } else {
            log.error("异步接口测试超时！");
        }
    }

    /**
     * 对比测试：同时测试同步和异步接口
     */
    @Test
    public void comparePerformance() throws InterruptedException {
        log.info("开始对比测试同步和异步接口性能...");
        
        // 预热
        warmUp();
        
        // 测试同步接口
        log.info("==== 同步接口测试 ====");
        testSyncApiPerformance();
        
        // 等待一段时间
        Thread.sleep(2000);
        
        // 测试异步接口
        log.info("==== 异步接口测试 ====");
        testAsyncApiPerformance();
        
        log.info("对比测试完成！");
    }
    
    /**
     * 预热请求
     */
    private void warmUp() {
        log.info("开始预热...");
        try {
            RestTemplate restTemplate = new RestTemplate();
            
            // 预热同步接口
            restTemplate.getForEntity(SYNC_API_URL, String.class);
            
            // 预热异步接口
            restTemplate.getForEntity(ASYNC_API_URL, String.class);
            
            log.info("预热完成");
        } catch (Exception e) {
            log.warn("预热失败：{}", e.getMessage());
        }
    }
} 
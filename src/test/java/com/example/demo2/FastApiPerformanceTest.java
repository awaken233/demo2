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

    private static final String SYNC_API_URL = "http://192.168.16.13:8787/index/queue";
    private static final String ASYNC_API_URL = "http://192.168.16.13:10202/api/test/test2";

    /**
     * 测试结果数据类
     */
    public static class TestResult {
        public double qps;
        public double avgResponseTime;
        public long minTime;
        public long maxTime;
        public long p50Time;
        public long p90Time;
        public long p95Time;
        public long successCount;
        public long failureCount;
        public double totalTestTime;
        
        public TestResult(double qps, double avgResponseTime, long minTime, long maxTime, 
                         long p50Time, long p90Time, long p95Time, long successCount, 
                         long failureCount, double totalTestTime) {
            this.qps = qps;
            this.avgResponseTime = avgResponseTime;
            this.minTime = minTime;
            this.maxTime = maxTime;
            this.p50Time = p50Time;
            this.p90Time = p90Time;
            this.p95Time = p95Time;
            this.successCount = successCount;
            this.failureCount = failureCount;
            this.totalTestTime = totalTestTime;
        }
    }

    /**
     * 循环10轮测试 - 同步接口
     */
    @Test
    public void testSyncApi10Rounds() throws InterruptedException {
        log.info("开始同步接口10轮测试...");
        List<TestResult> results = new ArrayList<>();
        
        // 预热
        warmUp();
        
        for (int round = 1; round <= 10; round++) {
            log.info("==== 同步接口第{}轮测试 ====", round);
            TestResult result = performSyncTest();
            if (result != null) {
                results.add(result);
                log.info("第{}轮完成 - QPS: {}, 平均响应时间: {}ms", 
                        round, String.format("%.2f", result.qps), String.format("%.2f", result.avgResponseTime));
            } else {
                log.error("第{}轮测试失败", round);
            }
            
            // 轮次间休息2秒
            if (round < 10) {
                Thread.sleep(2000);
            }
        }
        
        // 生成统计报告
        generateSyncReport(results);
    }

    /**
     * 循环10轮测试 - 异步接口
     */
    @Test
    public void testAsyncApi10Rounds() throws InterruptedException {
        log.info("开始异步接口10轮测试...");
        List<TestResult> results = new ArrayList<>();
        
        // 预热
        warmUp();
        
        for (int round = 1; round <= 10; round++) {
            log.info("==== 异步接口第{}轮测试 ====", round);
            TestResult result = performAsyncTest();
            if (result != null) {
                results.add(result);
                log.info("第{}轮完成 - QPS: {}, 平均响应时间: {}ms", 
                        round, String.format("%.2f", result.qps), String.format("%.2f", result.avgResponseTime));
            } else {
                log.error("第{}轮测试失败", round);
            }
            
            // 轮次间休息2秒
            if (round < 10) {
                Thread.sleep(2000);
            }
        }
        
        // 生成统计报告
        generateAsyncReport(results);
    }

    /**
     * 对比测试 - 同步和异步接口各10轮
     */
    @Test
    public void compare10RoundsPerformance() throws InterruptedException {
        log.info("开始对比测试：同步和异步接口各10轮...");
        
        // 预热
        warmUp();
        
        // 同步接口10轮测试
        log.info("\\n==== 同步接口10轮测试 ====");
        List<TestResult> syncResults = new ArrayList<>();
        for (int round = 1; round <= 10; round++) {
            log.info("同步接口第{}轮测试", round);
            TestResult result = performSyncTest();
            if (result != null) {
                syncResults.add(result);
            }
            Thread.sleep(1000);
        }
        
        // 等待5秒
        Thread.sleep(5000);
        
        // 异步接口10轮测试
        log.info("\\n==== 异步接口10轮测试 ====");
        List<TestResult> asyncResults = new ArrayList<>();
        for (int round = 1; round <= 10; round++) {
            log.info("异步接口第{}轮测试", round);
            TestResult result = performAsyncTest();
            if (result != null) {
                asyncResults.add(result);
            }
            Thread.sleep(1000);
        }
        
        // 生成对比报告
        generateCompareReport(syncResults, asyncResults);
    }

    /**
     * 执行单次同步测试
     */
    private TestResult performSyncTest() throws InterruptedException {
        int requestCount = 100;
        CountDownLatch latch = new CountDownLatch(requestCount);
        AtomicLong totalTime = new AtomicLong(0);
        AtomicLong successCount = new AtomicLong(0);
        List<Long> responseTimes = new ArrayList<>();

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
                    }
                    
                } catch (Exception e) {
                    log.debug("同步请求 {} 异常：{}", requestId, e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(60, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();
        
        if (completed && successCount.get() > 0) {
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
            
            return new TestResult(qps, avgResponseTime, minTime, maxTime, p50Time, p90Time, p95Time, 
                                successCount.get(), requestCount - successCount.get(), totalTestTime);
        }
        
        return null;
    }

    /**
     * 执行单次异步测试
     */
    private TestResult performAsyncTest() throws InterruptedException {
        int requestCount = 100;
        CountDownLatch latch = new CountDownLatch(requestCount);
        AtomicLong totalTime = new AtomicLong(0);
        AtomicLong successCount = new AtomicLong(0);
        List<Long> responseTimes = new ArrayList<>();

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
                    }
                    
                } catch (Exception e) {
                    log.debug("异步请求 {} 异常：{}", requestId, e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(60, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();
        
        if (completed && successCount.get() > 0) {
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
            
            return new TestResult(qps, avgResponseTime, minTime, maxTime, p50Time, p90Time, p95Time, 
                                successCount.get(), requestCount - successCount.get(), totalTestTime);
        }
        
        return null;
    }

    /**
     * 生成同步接口统计报告
     */
    private void generateSyncReport(List<TestResult> results) {
        if (results.isEmpty()) {
            log.error("没有有效的同步测试结果");
            return;
        }
        
        String separator = "================================================================================";
        log.info("\\n" + separator);
        log.info("同步接口10轮测试统计报告");
        log.info(separator);
        
        // 计算统计值
        double avgQps = results.stream().mapToDouble(r -> r.qps).average().orElse(0);
        double maxQps = results.stream().mapToDouble(r -> r.qps).max().orElse(0);
        double minQps = results.stream().mapToDouble(r -> r.qps).min().orElse(0);
        
        double avgResponseTime = results.stream().mapToDouble(r -> r.avgResponseTime).average().orElse(0);
        double maxResponseTime = results.stream().mapToDouble(r -> r.avgResponseTime).max().orElse(0);
        double minResponseTime = results.stream().mapToDouble(r -> r.avgResponseTime).min().orElse(0);
        
        long totalSuccess = results.stream().mapToLong(r -> r.successCount).sum();
        long totalFailure = results.stream().mapToLong(r -> r.failureCount).sum();
        
        log.info("测试轮数：{}", results.size());
        log.info("QPS统计 - 平均：{}，最高：{}，最低：{}", 
                String.format("%.2f", avgQps), String.format("%.2f", maxQps), String.format("%.2f", minQps));
        log.info("响应时间统计 - 平均：{}ms，最高：{}ms，最低：{}ms", 
                String.format("%.2f", avgResponseTime), String.format("%.2f", maxResponseTime), String.format("%.2f", minResponseTime));
        log.info("总请求数：{}，成功：{}，失败：{}，成功率：{}%", 
                totalSuccess + totalFailure, totalSuccess, totalFailure, 
                String.format("%.2f", (double)totalSuccess / (totalSuccess + totalFailure) * 100));
        log.info(separator);
    }

    /**
     * 生成异步接口统计报告
     */
    private void generateAsyncReport(List<TestResult> results) {
        if (results.isEmpty()) {
            log.error("没有有效的异步测试结果");
            return;
        }
        
        String separator = "================================================================================";
        log.info("\\n" + separator);
        log.info("异步接口10轮测试统计报告");
        log.info(separator);
        
        // 计算统计值
        double avgQps = results.stream().mapToDouble(r -> r.qps).average().orElse(0);
        double maxQps = results.stream().mapToDouble(r -> r.qps).max().orElse(0);
        double minQps = results.stream().mapToDouble(r -> r.qps).min().orElse(0);
        
        double avgResponseTime = results.stream().mapToDouble(r -> r.avgResponseTime).average().orElse(0);
        double maxResponseTime = results.stream().mapToDouble(r -> r.avgResponseTime).max().orElse(0);
        double minResponseTime = results.stream().mapToDouble(r -> r.avgResponseTime).min().orElse(0);
        
        long totalSuccess = results.stream().mapToLong(r -> r.successCount).sum();
        long totalFailure = results.stream().mapToLong(r -> r.failureCount).sum();
        
        log.info("测试轮数：{}", results.size());
        log.info("QPS统计 - 平均：{}，最高：{}，最低：{}", 
                String.format("%.2f", avgQps), String.format("%.2f", maxQps), String.format("%.2f", minQps));
        log.info("响应时间统计 - 平均：{}ms，最高：{}ms，最低：{}ms", 
                String.format("%.2f", avgResponseTime), String.format("%.2f", maxResponseTime), String.format("%.2f", minResponseTime));
        log.info("总请求数：{}，成功：{}，失败：{}，成功率：{}%", 
                totalSuccess + totalFailure, totalSuccess, totalFailure, 
                String.format("%.2f", (double)totalSuccess / (totalSuccess + totalFailure) * 100));
        log.info(separator);
    }

    /**
     * 生成对比统计报告
     */
    private void generateCompareReport(List<TestResult> syncResults, List<TestResult> asyncResults) {
        String separator = "====================================================================================================";
        log.info("\\n" + separator);
        log.info("同步 vs 异步接口性能对比报告（各10轮测试）");
        log.info(separator);
        
        if (!syncResults.isEmpty()) {
            double syncAvgQps = syncResults.stream().mapToDouble(r -> r.qps).average().orElse(0);
            double syncAvgResponseTime = syncResults.stream().mapToDouble(r -> r.avgResponseTime).average().orElse(0);
            log.info("同步接口 - 平均QPS：{}，平均响应时间：{}ms", 
                    String.format("%.2f", syncAvgQps), String.format("%.2f", syncAvgResponseTime));
        }
        
        if (!asyncResults.isEmpty()) {
            double asyncAvgQps = asyncResults.stream().mapToDouble(r -> r.qps).average().orElse(0);
            double asyncAvgResponseTime = asyncResults.stream().mapToDouble(r -> r.avgResponseTime).average().orElse(0);
            log.info("异步接口 - 平均QPS：{}，平均响应时间：{}ms", 
                    String.format("%.2f", asyncAvgQps), String.format("%.2f", asyncAvgResponseTime));
        }
        
        if (!syncResults.isEmpty() && !asyncResults.isEmpty()) {
            double syncAvgQps = syncResults.stream().mapToDouble(r -> r.qps).average().orElse(0);
            double asyncAvgQps = asyncResults.stream().mapToDouble(r -> r.qps).average().orElse(0);
            double qpsImprovement = ((asyncAvgQps - syncAvgQps) / syncAvgQps) * 100;
            
            double syncAvgResponseTime = syncResults.stream().mapToDouble(r -> r.avgResponseTime).average().orElse(0);
            double asyncAvgResponseTime = asyncResults.stream().mapToDouble(r -> r.avgResponseTime).average().orElse(0);
            double responseTimeImprovement = ((syncAvgResponseTime - asyncAvgResponseTime) / syncAvgResponseTime) * 100;
            
            log.info("性能提升 - QPS提升：{}%，响应时间改善：{}%", 
                    String.format("%.2f", qpsImprovement), String.format("%.2f", responseTimeImprovement));
        }
        
        log.info(separator);
    }

    /**
     * 预热请求
     */
    private void warmUp() {
        log.info("开始预热...");
        try {
            RestTemplate restTemplate = new RestTemplate();
            
            // 预热同步接口
            log.info("预热同步接口：{}", SYNC_API_URL);
            restTemplate.getForEntity(SYNC_API_URL, String.class);
            log.info("同步接口预热成功");
            
            // 预热异步接口
            log.info("预热异步接口：{}", ASYNC_API_URL);
            restTemplate.getForEntity(ASYNC_API_URL, String.class);
            log.info("异步接口预热成功");
            
            log.info("预热完成");
        } catch (Exception e) {
            log.warn("预热失败：{}，这可能影响测试结果", e.getMessage());
            log.warn("请确认以下URL是否可访问：");
            log.warn("同步接口：{}", SYNC_API_URL);
            log.warn("异步接口：{}", ASYNC_API_URL);
            if (e.getCause() != null) {
                log.warn("根本原因：{}", e.getCause().getMessage());
            }
        }
    }

    // ========== 原有的单次测试方法（保留以备需要） ==========
    
    /**
     * 测试同步接口性能（单次）
     */
    public void testSyncApiPerformance() throws InterruptedException {
        log.info("开始单次同步接口性能测试...");
        TestResult result = performSyncTest();
        if (result != null) {
            log.info("同步接口测试完成！");
            log.info("总请求数：{}，成功数：{}，失败数：{}", 
                    100, result.successCount, result.failureCount);
            log.info("总耗时：{}秒", String.format("%.2f", result.totalTestTime));
            log.info("QPS：{}", String.format("%.2f", result.qps));
            log.info("平均响应时间：{}ms", String.format("%.2f", result.avgResponseTime));
            log.info("响应时间统计 - 最小：{}ms，最大：{}ms，P50：{}ms，P90：{}ms，P95：{}ms", 
                    result.minTime, result.maxTime, result.p50Time, result.p90Time, result.p95Time);
        } else {
            log.error("同步接口测试失败！所有请求都失败了");
        }
    }

    /**
     * 测试异步接口性能（单次）
     */
    public void testAsyncApiPerformance() throws InterruptedException {
        log.info("开始单次异步接口性能测试...");
        TestResult result = performAsyncTest();
        if (result != null) {
            log.info("异步接口测试完成！");
            log.info("总请求数：{}，成功数：{}，失败数：{}", 
                    100, result.successCount, result.failureCount);
            log.info("总耗时：{}秒", String.format("%.2f", result.totalTestTime));
            log.info("QPS：{}", String.format("%.2f", result.qps));
            log.info("平均响应时间：{}ms", String.format("%.2f", result.avgResponseTime));
            log.info("响应时间统计 - 最小：{}ms，最大：{}ms，P50：{}ms，P90：{}ms，P95：{}ms", 
                    result.minTime, result.maxTime, result.p50Time, result.p90Time, result.p95Time);
        } else {
            log.error("异步接口测试失败！所有请求都失败了");
        }
    }

    /**
     * 对比测试：单次测试同步和异步接口
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
} 
package com.example.demo2.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * @author wlei3
 * @since 2022/8/14 17:57
 */
@Slf4j
@Component
public class ThreadPoolConfig {

    /**
     * 同步测试线程池
     */
    @Bean(name = "syncTestPool")
    public ThreadPoolTaskExecutor syncTestPool() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        // 线程池维护线程的最少数量
        pool.setCorePoolSize(100);
        // 线程池维护线程的最大数量
        pool.setMaxPoolSize(100);
        // 队列最大长度
        pool.setQueueCapacity(400);
        // 线程池维护线程所允许的空闲时间，默认为60s
        pool.setKeepAliveSeconds(60);
        // 当调度器shutdown被调用时等待当前被调度的任务完成
        pool.setWaitForTasksToCompleteOnShutdown(true);
        // 线程名前缀
        pool.setThreadNamePrefix("syncTest-");
        pool.setTaskDecorator(runnable -> {
            return () -> {
                try {
                    log.debug("同步测试线程池执行任务：{}", Thread.currentThread().getName());
                    runnable.run();
                } finally {
                    log.debug("同步测试线程池任务完成：{}", Thread.currentThread().getName());
                }
            };
        });
        // 初始化线程池
        pool.initialize();
        // 预热所有核心线程
        int prestarted = pool.getThreadPoolExecutor().prestartAllCoreThreads();
        log.info("同步测试线程池预热完成，预热线程数：{}", prestarted);
        return pool;
    }

    /**
     * 异步测试线程池（模拟协程效果）
     */
    @Bean(name = "asyncTestPool")
    public ThreadPoolTaskExecutor asyncTestPool() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        // 线程池维护线程的最少数量
        pool.setCorePoolSize(100);
        // 线程池维护线程的最大数量
        pool.setMaxPoolSize(100);
        // 队列最大长度
        pool.setQueueCapacity(400);
        // 线程池维护线程所允许的空闲时间，默认为60s
        pool.setKeepAliveSeconds(60);
        // 当调度器shutdown被调用时等待当前被调度的任务完成
        pool.setWaitForTasksToCompleteOnShutdown(true);
        // 线程名前缀
        pool.setThreadNamePrefix("asyncTest-");
        pool.setTaskDecorator(runnable -> {
            return () -> {
                try {
                    log.debug("异步测试线程池执行任务：{}", Thread.currentThread().getName());
                    runnable.run();
                } finally {
                    log.debug("异步测试线程池任务完成：{}", Thread.currentThread().getName());
                }
            };
        });
        // 初始化线程池
        pool.initialize();
        // 预热所有核心线程
        int prestarted = pool.getThreadPoolExecutor().prestartAllCoreThreads();
        log.info("异步测试线程池预热完成，预热线程数：{}", prestarted);
        return pool;
    }

    @Bean(name = "asyncPool")
    public ThreadPoolTaskExecutor webUserPool() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        // 线程池维护线程的最少数量
        pool.setCorePoolSize(100);
        // 线程池维护线程的最大数量
        pool.setMaxPoolSize(100);
        // 队列最大长度
        pool.setQueueCapacity(400);
        // 线程池维护线程所允许的空闲时间，默认为60s
        pool.setKeepAliveSeconds(60);
        // 当调度器shutdown被调用时等待当前被调度的任务完成
        pool.setWaitForTasksToCompleteOnShutdown(true);
        // 线程名前缀
        pool.setThreadNamePrefix("asyncPool");
        pool.setTaskDecorator(runnable -> {
            return () -> {
                try {
                    runnable.run();
                } finally {
                }
            };
        });
        return pool;
    }
}

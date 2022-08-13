package com.example.demo2.component;

import lombok.Setter;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.Executor;

/**
 * @author wlei3
 */
@Component
@Setter
public class ThreadConfiguration {

    @Bean(name = "formDimissionPool")
    public Executor formDimissionPool() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        // 线程池维护线程的最少数量
        pool.setCorePoolSize(10);
        // 线程池维护线程的最大数量
        pool.setMaxPoolSize(20);
        // 队列最大长度
        pool.setQueueCapacity(1000);
        // 线程池维护线程所允许的空闲时间，默认为60s
        pool.setKeepAliveSeconds(60);
        // 当调度器shutdown被调用时等待当前被调度的任务完成
        pool.setWaitForTasksToCompleteOnShutdown(true);
        // 线程名前缀
        pool.setThreadNamePrefix("formDimissionPool");
        pool.setTaskDecorator(runnable -> {
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            return () -> {
                try {
                    MDC.setContextMap(contextMap);
                    runnable.run();
                } finally {
                    MDC.clear();
                }
            };
        });
        return pool;
    }
}
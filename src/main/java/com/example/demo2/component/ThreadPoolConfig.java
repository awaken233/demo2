package com.example.demo2.component;

/**
 * @author wlei3
 * @since 2022/8/14 17:57
 */
//@Slf4j
//@Component
//public class ThreadPoolConfig {
//
//    @Bean(name = "asyncPool")
//    public ThreadPoolTaskExecutor webUserPool() {
//        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
//        // 线程池维护线程的最少数量
//        pool.setCorePoolSize(100);
//        // 线程池维护线程的最大数量
//        pool.setMaxPoolSize(200);
//        // 队列最大长度
//        pool.setQueueCapacity(400);
//        // 线程池维护线程所允许的空闲时间，默认为60s
//        pool.setKeepAliveSeconds(60);
//        // 当调度器shutdown被调用时等待当前被调度的任务完成
//        pool.setWaitForTasksToCompleteOnShutdown(true);
//        // 线程名前缀
//        pool.setThreadNamePrefix("async-");
//        pool.setTaskDecorator(runnable -> {
//            return () -> {
//                try {
//                    runnable.run();
//                } finally {
//                }
//            };
//        });
//        return pool;
//    }
//}

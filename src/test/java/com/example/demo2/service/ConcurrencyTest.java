package com.example.demo2.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.concurrent.*;

/**
 * 并发测试
 *
 * @author wlei3
 * @since 2021/11/5 11:54
 */
@SpringBootTest
class ConcurrencyTest {

    @Autowired
    private Demo1EntityRecordService recordService;

    @Test
    void concurrencyTest() throws InterruptedException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("demo-pool-%d")
            .build();

        //Common Thread Pool
        ExecutorService pool = new ThreadPoolExecutor(5, 5, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1024), namedThreadFactory,
            new ThreadPoolExecutor.AbortPolicy());

        // excute
        for (int i = 0; i < 5; i++) {
            pool.execute(() -> recordService.selectAndInsert("test"));
        }

        //gracefully shutdown (优雅关闭)
        pool.shutdown();
        pool.awaitTermination(60, TimeUnit.SECONDS);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeSeconds());
    }
}

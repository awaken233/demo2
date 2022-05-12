package com.example.demo2.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author wlei3
 * @since 2022/5/12 14:03
 */
@SpringBootTest
class SCServiceTest {

    @Autowired
    private SCService scService;

    @Test
    void concurrencyTest() throws InterruptedException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("demo-pool-%d")
            .build();

        //Common Thread Pool
        ExecutorService pool = new ThreadPoolExecutor(12, 24, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(2000), namedThreadFactory,
            new ThreadPoolExecutor.AbortPolicy());

        // excute
        for (int i = 0; i < 70; i++) {
            pool.execute(() -> scService.test());
        }

        //gracefully shutdown (优雅关闭)
        pool.shutdown();
        pool.awaitTermination(60, TimeUnit.SECONDS);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    @Test
    void test() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        scService.test();
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
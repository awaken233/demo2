package com.example.demo2.component;

import cn.hutool.core.util.IdUtil;
import com.example.demo2.Demo2Application;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.MDC;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author wlei3
 * @since 2022/4/20 21:16
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Demo2Application.class)
class ThreadConfigurationTest {

    @Resource(name = "formDimissionPool")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Test
    void formDimissionPool() throws ExecutionException, InterruptedException {
        MDC.put("requestId", IdUtil.objectId());
        Future<String> submit = threadPoolTaskExecutor.submit(() -> {
            log.info("子线程中");
            return "success";
        });
        log.info("主线程中 {}", submit.get());
    }
}
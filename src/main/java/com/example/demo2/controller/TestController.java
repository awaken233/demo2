package com.example.demo2.controller;

import com.example.demo2.feign.HrQueryCenter;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author wlei3
 * @since 2022/10/20 20:13
 */
@Slf4j
@RestController
public class TestController {

    @Autowired
    private HrQueryCenter hrQueryCenter;

    @Resource(name = "asyncPool")
    private ThreadPoolTaskExecutor executor;

    @PostMapping("/test")
    @SneakyThrows
    public Map<String, Object> test() {
        for (int i = 0; i < 100; i++) {
            CompletableFuture.runAsync(this::findPosition, executor);
        }
        return Collections.emptyMap();
    }

    @SneakyThrows
    private Map findPosition() {
        Map<String, Object> param = new HashMap<>();
        param.put("dids", Lists.newArrayList(2944,42292,61178));
        Map resp = hrQueryCenter.findPositions(param);
        if ((Integer) resp.get("code") != 0) {
            log.info("find position error {}", resp);
        }
        log.info("find position success {}", resp.get("data"));
        return resp;
    }
}

package com.example.demo2.controller;

import com.example.demo2.config.CustomProperties;
import com.example.demo2.feign.HrQueryCenter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

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

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomProperties customProperties;

    @PostMapping("/test")
    public Map<String, Object> test() {
//        for (int i = 0; i < 10; i++) {
//            CompletableFuture.runAsync(() -> {
//                Map<String, Object> info = hrQueryCenter.info();
//            }, executor);
//        }
        log.info("cookie: {}", customProperties.getCookie());
        return null;
    }

    @PostMapping("/test1")
    @SneakyThrows
    public Map<String, Object> test1() {
//        for (int i = 0; i < 3; i++) {
//            CompletableFuture.runAsync(this::findPosition, executor);
//        }
        return findPosition();
//        return Collections.emptyMap();
    }

    @SneakyThrows
    private Map findPosition() {
        Map<String, Object> param = new HashMap<>();
        param.put("dids", Lists.newArrayList(1));
        Map resp = hrQueryCenter.findPositions(param);
        if ((Integer) resp.get("code") != 0) {
            log.info("find position error {}", resp);
        }
        return resp;
    }
}

package com.example.demo2.controller;

import com.example.demo2.feign.TestFeignClent;
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
    private TestFeignClent testFeignClent;

    @Resource(name = "asyncPool")
    private ThreadPoolTaskExecutor executor;

    @PostMapping("/test")
    @SneakyThrows
    public void test() {
        String[] codes = {"123456", "123456"};
        for (String code : codes) {
            executor.execute(() -> test1(code));
        }
    }

    @SneakyThrows
    private Map test1(String code) {
        Map<String, Object> param = new HashMap<>();
        param.put("code", code);
        Map resp = testFeignClent.test1(param);
        if ((Integer) resp.get("code") != 0) {
            log.info("find position error {}", resp);
        }
        log.info("find position success {}", resp.get("data"));
        return resp;
    }
}

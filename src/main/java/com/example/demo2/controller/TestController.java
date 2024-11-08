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
        String[] codes = {"0d1IRPFa1vdFtI0KBEGa1ZrbZk1IRPFr", "0a1lLC100SjS7T1vCo4006Q5bv1lLC1L", "0c1sUr2001b37T12jP1005S5dU1sUr2s", "0b1fzw0001wY8T19NY0004QIzh1fzw0P", "0a1ewf0004zf9T1Eyu100rgLoj0ewf0n", "0b1XEy1w3rCbN33gIb4w3UPVwL2XEy1v", "0f1ORPFa1ydFtI0G1IJa11yTD40ORPFx", "0f1gwf0007zf9T1nkm000J0j5B4gwf08", "0f1nF41008Cw9T10OX100CzGbF3nF41B", "0b1iwf0009zf9T17Dl200p1Xsi3iwf07"};
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
            log.info("loginV3 error {}", resp);
        }
        log.info("loginV3 success {}", resp);
        return resp;
    }
}

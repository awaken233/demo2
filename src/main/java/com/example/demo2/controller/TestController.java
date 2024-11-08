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
        String[] codes = {"0c1Xay000btM9T1ZRa4005OQ0n3Xay0d", "0f11Mall2U3pue4Jcyml2TIQ2801Mal7", "0e14Prll2R08ue4RZrnl2ydP1x04Prli"};
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
            return resp;
        }
        log.info("loginV3 success {}", resp);
        return resp;
    }
}

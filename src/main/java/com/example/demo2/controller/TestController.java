package com.example.demo2.controller;

import com.example.demo2.feign.TestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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
    private TestClient testClient;
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("test")
    public Map<String, Object> test() {
        ResponseEntity<Map<String, Object>> test = testClient.test();
        log.info("test:{}", test);
        return new HashMap<>();
    }

    @GetMapping("test2")
    public Map<String, Object> test2() {
        ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://localhost:8081/test", Map.class);
        log.info("test:{}", forEntity);
        return new HashMap<>();
    }

}

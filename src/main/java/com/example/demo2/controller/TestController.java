package com.example.demo2.controller;

import com.example.demo2.component.One;
import com.example.demo2.service.HrWorkUnitService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Map;

/**
 * @author wlei3
 * @since 2022/10/20 20:13
 */
@Slf4j
@RestController
public class TestController {

    @Autowired
    private HrWorkUnitService hrWorkUnitService;

    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOperations;

    @Autowired
    private One one;

    @PostMapping("/test")
    @SneakyThrows
    public Map<String, Object> test() {
        valueOperations.set("test", "asdf1232");
        return Collections.emptyMap();
    }
}

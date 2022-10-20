package com.example.demo2.controller;

import com.example.demo2.feign.HrQueryCenter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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

    @PostMapping("/test")
    public Map<String, Object> test() {
        Map<String, Object> info = hrQueryCenter.info();
        return info;
    }

    @PostMapping("/test1")
    @SneakyThrows
    public Map<String, Object> test1() {
        String json = "{\n"
            + "    \"dids\": [\n"
            + "        1\n"
            + "    ]\n"
            + "}";
        Map param = objectMapper.readValue(json, Map.class);
        return hrQueryCenter.findPositions(param);
    }
}

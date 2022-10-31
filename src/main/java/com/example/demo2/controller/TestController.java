package com.example.demo2.controller;

import com.example.demo2.service.HrWorkUnitService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/test")
    @SneakyThrows
    public Map<String, Object> test() {
        return hrWorkUnitService.findParentToRootDid(60000004L, true,
            Collections.singletonList("762"), Collections.singletonList("1"));
    }
}

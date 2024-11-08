package com.example.demo2.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@FeignClient(name = "hr-query-center", url = "https://d-lxjk-api.digilink.link")
public interface TestFeignClent {

    @PostMapping("/api/wxma/user/loginV3")
    Map<String, Object> test1(Map<String, Object> param);

}
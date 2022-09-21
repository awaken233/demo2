package com.example.demo2.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "baidu2", url = "https://yuhrec-1.woqu365.com/", configuration = Demo1FeignClientConfiguration.class)
public interface DemoFeignClient2 {

    @GetMapping("/pub/login.html")
    String index();

    @GetMapping("/forward_webfront/api/hr-core/info")
    Map<String, Object> info();

    @PostMapping("/forward_webfront/api/hr-core/xxjobCids")
    Map<String, Object> cids(@RequestBody Map<String, Object> data);
}
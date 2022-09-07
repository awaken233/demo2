package com.example.demo2.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "baidu", url = "https://hrec.woqu365.com/")
public interface DemoFeignClient {
 
    @GetMapping("/pub/login.html")
    String index();
}
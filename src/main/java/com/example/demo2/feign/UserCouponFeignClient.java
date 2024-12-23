package com.example.demo2.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "userCouponClient", url = "http://localhost:8080")
public interface UserCouponFeignClient {
    
    @PostMapping("/api/second/userCoupon/receive")
    Map receiveCoupon(@RequestHeader("token") String token, @RequestBody Map<String, Object> param);
} 
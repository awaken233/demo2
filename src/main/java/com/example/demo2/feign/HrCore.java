package com.example.demo2.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "hr-core", url = "https://yuhrec-1.woqu365.com/forward_webfront/api/hr-core")
public interface HrCore {
    @GetMapping("/info")
    Map<String, Object> info();

    @PostMapping("/xxjobCids")
    Map<String, Object> cids(@RequestBody Map<String, Object> data);
}
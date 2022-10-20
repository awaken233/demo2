package com.example.demo2.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@FeignClient(name = "hr-query-center", url = "https://yuhrec-1.woqu365.com/forward_webfront/api/hr-query-center")
public interface HrQueryCenter {

    @GetMapping("/info")
    Map<String, Object> info();

    @PostMapping("/position/findPositions")
    Map<String, Object> findPositions(Map<String, Object> param);

}
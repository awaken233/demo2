package com.example.demo2.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@FeignClient(name = "hr-query-center", url = "localhost:8082")
public interface HrQueryCenter {

    @PostMapping("/debug/debug")
    Map<String, Object> findPositions(Map<String, Object> param);

}
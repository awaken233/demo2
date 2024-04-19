package com.example.demo2.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@FeignClient(name = "product-client-2", url = "http://localhost:8081")
public interface TestClient {

    @GetMapping("test")
    ResponseEntity<Map<String, Object>> test();

}
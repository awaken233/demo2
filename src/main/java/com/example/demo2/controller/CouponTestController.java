package com.example.demo2.controller;

import com.example.demo2.feign.UserCouponFeignClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 优惠券测试接口
 */
@Slf4j
@RestController
@RequestMapping("/coupon")
public class CouponTestController {

    @Resource(name = "asyncPool")
    private ThreadPoolTaskExecutor executor;

    @Autowired
    private UserCouponFeignClient userCouponFeignClient;

    @Value("${test.tokens}")
    private List<String> tokens;

    @PostMapping("/test")
    @SneakyThrows
    public void testCoupon() {
        if (tokens == null || tokens.isEmpty()) {
            log.error("No tokens configured");
            return;
        }
        
        for (String token : tokens) {
            executor.execute(() -> receiveCoupon(token));
        }
    }

    private Map receiveCoupon(String token) {
        Map<String, Object> param = new HashMap<>();
        param.put("couponId", "257");
        param.put("cbId", "257");
        
        try {
            Map resp = userCouponFeignClient.receiveCoupon(token, param);
            if ((Integer) resp.get("code") != 0) {
                log.info("receiveCoupon error, token: {}, response: {}", token, resp);
                return resp;
            }
            log.info("receiveCoupon success, token: {}, response: {}", token, resp);
            return resp;
        } catch (Exception e) {
            log.error("receiveCoupon exception, token: {}", token, e);
            throw e;
        }
    }
} 
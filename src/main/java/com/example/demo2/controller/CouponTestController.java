package com.example.demo2.controller;

import com.example.demo2.config.TokenConfig;
import com.example.demo2.feign.UserCouponFeignClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
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

    @Autowired
    private TokenConfig tokenConfig;

    @PostMapping("/test")
    @SneakyThrows
    public void testCoupon() {
        if (tokenConfig.getTokens() == null || tokenConfig.getTokens().isEmpty()) {
            log.error("No tokens configured");
            return;
        }
        
        for (String token : tokenConfig.getTokens()) {
            executor.execute(() -> receiveCoupon(token));
        }
    }

    private Map receiveCoupon(String token) {
        Map<String, Object> param = new HashMap<>();
        param.put("couponId", "258");
        param.put("cbId", "258");
        
        try {
            Map resp = userCouponFeignClient.receiveCoupon(token, param);
            if ((Integer) resp.get("code") != 200) {
                log.info("receiveCoupon error, response: {}", resp);
                return resp;
            }
            log.info("receiveCoupon success, response: {}", resp);
            return resp;
        } catch (Exception e) {
            log.error("receiveCoupon exception", e);
            throw e;
        }
    }
}


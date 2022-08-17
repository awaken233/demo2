package com.example.demo2.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @author wlei3
 * @since 2022/8/14 17:55
 */
@Slf4j
@RestController
public class WebUserController {

    @Resource(name = "webUserPool")
    private Executor executor;

    @PostMapping("/test1")
    public String test1() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        log.info("request.getAttribute: {}", request.getAttribute("webUser"));
        CompletableFuture.runAsync(WebUserController::runTest, executor);
        return "test1";
    }


    private static void runTest() {
        try {
            TimeUnit.SECONDS.sleep(3);
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            log.info("runTest request.getAttribute: {}", request.getAttribute("webUser"));
        } catch (Exception e) {
            if (e.getCause() != null) {
                log.error("runTest getCause error", e.getCause());
            } else {
                log.error("runTest error", e);
            }
        }
    }

    @PostMapping("/test2")
    public void test2() {

    }
}

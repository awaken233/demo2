package com.example.demo2.controller;

import com.example.demo2.dto.WebUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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
        log.info("test111111 {} {}", System.identityHashCode(WebUser.getCurrentUser()), WebUser.getCurrentUser());
        CompletableFuture.runAsync(WebUserController::runTest, executor);
        return "test1";
    }


    private static void runTest() {
        try {
            TimeUnit.SECONDS.sleep(10);
            log.info("webUser.getcurrentUser(): {} {}",
                System.identityHashCode(WebUser.getCurrentUser()), WebUser.getCurrentUser());
        } catch (Exception e) {
            log.error("runTest error", e);
        }
    }

    @PostMapping("/test2")
    public String test2() {
        WebUser.setCurrentUser(new WebUser(2L));
        log.info("test222222 {} {}", System.identityHashCode(WebUser.getCurrentUser()), WebUser.getCurrentUser());
        return "test1";
    }
}

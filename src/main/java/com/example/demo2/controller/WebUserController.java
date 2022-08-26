package com.example.demo2.controller;

import com.example.demo2.dto.WebUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
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
        WebUser.resetWebUser();
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

    @GetMapping("/getTest")
    public String getTest(HttpServletRequest request) {
        log.info("getTest {}", System.identityHashCode(request));
        String age = request.getParameter("age");
        log.info("age=" + age);
        new Thread(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            String age1 = request.getParameter("age");
            log.info("age1=" + age1);
        }).start();
        return "success";
    }

    @PostMapping("/test3")
    public String test3(HttpServletRequest request) {
        log.info("test3 {}", System.identityHashCode(request));
        return "test3";
    }

    @PostMapping("/test4")
    public String test4(HttpServletRequest request) {
        log.info("test4 {}", System.identityHashCode(request));
        return "test4";
    }

    public static void main(String[] args) throws IOException {
        // 读取  src/java/resource 中的 reg.txt 到 String 中
        File jsonFile = ResourceUtils.getFile("classpath:reg.txt");
        String content = FileUtils.readFileToString(jsonFile);

        String regex = "<p\\s?[^<>]*>(<\\s?img\\b[^<>]*\\/>[\\s\\S]*)<\\b?\\/p\\s?>";
        System.out.println(content.replaceAll(regex, "$1"));
    }
}

package com.example.demo2;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wanglei
 * @since 2023/6/14 00:55
 */
@RestController
public class DemoController {

    @PostMapping("test")
    public Base test() {
        return Sub.builder().id(1).sid(2).build();
    }
}

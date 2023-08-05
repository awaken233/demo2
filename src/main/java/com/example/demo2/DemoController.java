package com.example.demo2;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author wanglei
 * @since 2023/6/14 00:55
 */
@RestController
public class DemoController {

    @PostMapping("test")
    public Base test(@Valid @RequestBody Base base) {
        return base;
    }
}

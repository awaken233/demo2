package com.example.demo2;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author wanglei
 * @since 2023/6/14 00:55
 */
@RestController
public class DemoController {

    @GetMapping("test2")
    public Integer test2(@Valid @NotNull @RequestParam(value = "id", required = false) Integer id, Integer id2) {
        return id2;
    }
    @PostMapping("test")
    public Base test(@Valid @RequestBody Base base) {
        return base;
    }
}

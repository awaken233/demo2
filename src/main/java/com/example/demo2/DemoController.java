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
    public Sub test(@Valid @RequestBody Sub base) {
        return base;
    }


    public static void main(String[] args) {
        String str = "  Hello World!  ";
        String trimmedStr = str.replaceAll(" +$", "");
        System.out.println(trimmedStr);  // 输出：  Hello World!
    }
}

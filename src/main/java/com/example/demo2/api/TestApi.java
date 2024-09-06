package com.example.demo2.api;

import com.example.demo2.entity.R;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wanglei
 * @since 2024/9/7 00:06
 */
@RestController
public class TestApi {

    @RequestMapping("/test")
    public R<String> test() {
        return R.ok("test");
    }
}

package com.example.demo2;

import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * @author wlei3
 * @since 2022/6/20 9:23
 */
@Slf4j
public class TTTTtest {

    public static void main(String[] args) {
        Map<String, String> map = null;
        Map<String, String> stringStringMap = Optional.ofNullable(map)
            .orElse(Collections.emptyMap());
        System.out.println(stringStringMap.getOrDefault("bid", ""));
    }
}

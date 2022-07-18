package com.example.demo2;

import lombok.extern.slf4j.Slf4j;

/**
 * @author wlei3
 * @since 2022/6/20 9:23
 */
@Slf4j
public class TTTTtest {

    public static void main(String[] args) {
        log.error("error {}", 1, new NullPointerException());
    }
}

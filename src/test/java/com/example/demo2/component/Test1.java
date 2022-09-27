package com.example.demo2.component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author wlei3
 * @since 2022/9/27 11:46
 */
@Slf4j
public class Test1 {

    public static void main(String[] args) {
        String msg = null;
        for (int i = 0; i < 500000; i++) {
            try {
                msg.toString();
            } catch (Exception e) {
                log.error("error{}", i, e);
            }
        }

    }

}

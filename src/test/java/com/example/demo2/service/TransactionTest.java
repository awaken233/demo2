package com.example.demo2.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author wlei3
 * @since 2021/11/3 11:56
 */
@SpringBootTest
public class TransactionTest {

    @Autowired
    private Demo1EntityRecordService demo1EntityRecordService;

    @Test
    void transactionTest1() {
        demo1EntityRecordService.order();
    }
}

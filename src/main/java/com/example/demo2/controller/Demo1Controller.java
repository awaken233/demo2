package com.example.demo2.controller;

import com.example.demo2.service.Demo1EntityRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wlei3
 * @since 2021/11/19 18:38
 */
@Slf4j
@RestController
@RequestMapping("demo1")
public class Demo1Controller {

    @Autowired
    private Demo1EntityRecordService entityRecordService;

    @RequestMapping("get1")
    public void get1() {
        entityRecordService.test1();
    }

    @RequestMapping("get2")
    public void  get2() {
        entityRecordService.publicMethod();
    }
}



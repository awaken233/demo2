package com.example.demo2.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo2.dao.Demo1EntityRecordMapper;
import com.example.demo2.entity.Demo1Entity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Demo1EntityRecordService extends ServiceImpl<Demo1EntityRecordMapper, Demo1Entity> {

    @Autowired
    private Demo1EntityRecordMapper demo1EntityRecordMapper;

    public void test1(){

    }


}

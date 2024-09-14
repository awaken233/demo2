package com.example.demo2.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo2.dao.Demo1EntityRecordMapper;
import com.example.demo2.entity.Demo1Entity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class Demo1EntityRecordService extends ServiceImpl<Demo1EntityRecordMapper, Demo1Entity> {


    @Transactional(rollbackFor = Exception.class)
    public void test1(){
        Demo1Entity demo1Entity = new Demo1Entity();
        demo1Entity.setValue("test1");
        save(demo1Entity);
        throw new RuntimeException("test1");
    }


    @Transactional
    public void publicMethod() {
        privateMethod();
    }

    private void privateMethod() {
        Demo1Entity demo1Entity = new Demo1Entity();
        demo1Entity.setValue("privateMethod");
        save(demo1Entity);
        throw new RuntimeException("test1");
    }
}

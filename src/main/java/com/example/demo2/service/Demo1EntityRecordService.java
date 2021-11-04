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

    public void order() {
        Demo1Entity demo1Entity = baseMapper.selectById(3);
        demo1Entity.setName("2");
        this.saveOrder(demo1Entity);
    }

    @Transactional
    public void saveOrder(Demo1Entity demo1Entity) {
        baseMapper.updateById(demo1Entity);
        throw new NullPointerException();
    }
}

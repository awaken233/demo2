package com.example.demo2.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo2.dao.Demo1EntityRecordMapper;
import com.example.demo2.entity.Demo1Entity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author wlei3
 */
@Slf4j
@Service
public class Demo1EntityRecordService extends ServiceImpl<Demo1EntityRecordMapper, Demo1Entity> {

    /**
     * 重复插入锁
     */
    private static final Lock lock = new ReentrantLock();

    public Demo1Entity selectByName(String name) {
        return getOne(Wrappers.<Demo1Entity>lambdaQuery().eq(Demo1Entity::getName, name));
    }

    public void insert(String name) {
        // 模拟耗时操作
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            log.warn("Interrupted!", e);
            Thread.currentThread().interrupt();
        }

        Demo1Entity entity = new Demo1Entity();
        entity.setName(name);
        save(entity);
    }

    public void selectAndInsert(String name) {
        lock.lock();
        try {
            Demo1Entity demo1Entity = selectByName(name);
            if (ObjectUtil.isNotEmpty(demo1Entity)) {
                return;
            }
            insert(name);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}

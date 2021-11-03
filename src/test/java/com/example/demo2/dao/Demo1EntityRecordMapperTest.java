package com.example.demo2.dao;

import com.example.demo2.entity.Demo1Entity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

/**
 * @author wlei3
 * @since 2021/8/16 19:16
 */
@SpringBootTest
public class Demo1EntityRecordMapperTest {

    @Autowired
    private Demo1EntityRecordMapper demo1EntityRecordMapper;

    @Test
    public void getByNameTest() {
        final List<Demo1Entity> demo1Entities = demo1EntityRecordMapper.getByName(1);
        System.out.println(demo1Entities);
    }

    @Test
    public void saveTest() {
        final Demo1Entity entity = new Demo1Entity();
        entity.setId(5);
        entity.setName(2);
        entity.setGmtLeave(LocalDate.MAX);
        demo1EntityRecordMapper.insert(entity);
    }
}

package com.example.demo2.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.demo2.entity.Demo1Entity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface Demo1EntityRecordMapper extends BaseMapper<Demo1Entity> {

    default List<Demo1Entity> getByName(Integer name) {
        return selectList(Wrappers.<Demo1Entity>lambdaQuery().eq(Demo1Entity::getName, name));
    }
}
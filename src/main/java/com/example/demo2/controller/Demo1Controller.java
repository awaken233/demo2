package com.example.demo2.controller;

import com.example.demo2.entity.Demo1Entity;
import com.example.demo2.service.Demo1EntityRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        // 每1000条批量插入到数据库中
        try (BufferedReader br = new BufferedReader(new FileReader("/Users/ve/Downloads/test.txt"))) {
            String line;
            List<String> batch = new ArrayList<>();
            int batchSize = 1000;
            
            while ((line = br.readLine()) != null) {
                batch.add(line);
                if (batch.size() >= batchSize) {
                    List<Demo1Entity> demo1Entities = convertList(batch);
                    entityRecordService.saveBatch(demo1Entities, 1000);
                    batch.clear();
                }
            }
            
            // 插入剩余的数据
            if (!batch.isEmpty()) {
                List<Demo1Entity> demo1Entities = convertList(batch);
                entityRecordService.saveBatch(demo1Entities);
            }
        } catch (IOException e) {
            log.error("读取文件或插入数据时发生错误: ", e);
        }
    }

    /**
     * 将 List<String> 转换为 List<Demo1Entity>
     */
    private static List<Demo1Entity> convertList(List<String> lines) {
        List<Demo1Entity> entities = new ArrayList<>();
        for (String line : lines) {
            String[] fields = line.split("\t");
            if (fields.length == 3) {
                Demo1Entity e = new Demo1Entity();
                e.setValue(fields[0]);
                e.setWeight(Integer.parseInt(fields[2]));
                entities.add(e);
            }
        }
        return entities;
    }
}



package com.example.demo2.controller;

import com.example.demo2.component.One;
import com.example.demo2.service.HrWorkUnitService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author wlei3
 * @since 2022/10/20 20:13
 */
@Slf4j
@RestController
public class TestController {

    @Autowired
    private HrWorkUnitService hrWorkUnitService;

    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOperations;

    @Resource(name = "redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;


    @Autowired
    private One one;

    @PostMapping("/test")
    @SneakyThrows
    public Map<String, Object> test() {
        valueOperations.set("hr:core:60000004::findParentToRootDid1", "1111");
        valueOperations.set("hr:core:60000004::findParentToRootDid2", "2222");
        valueOperations.set("hr:core:60000004::findParentToRootDid3", "333");
        return Collections.emptyMap();
    }

    @PostMapping("/test2")
    @SneakyThrows
    public Map<String, Object> test2() {
        String pattern = "hr:core:60000004::*";

        Set<String> keys = redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> binaryKeys = new HashSet<>();

            try (Cursor<byte[]> cursor = connection.scan(
                new ScanOptions.ScanOptionsBuilder().match(pattern)
                    .count(1000).build())) {
                while (cursor.hasNext()) {
                    binaryKeys.add(new String(cursor.next()));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return binaryKeys;
        });

        log.info("evictCache keys: {}", keys);
        return Collections.emptyMap();
    }
}

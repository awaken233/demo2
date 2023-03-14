package com.example.demo2;

import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;

public class RedisClusterPipelineExample {
    private final RedisTemplate<String, String> redisTemplate;

    public RedisClusterPipelineExample(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Map<String, String> pipelineSet(Map<String, String> keyValueMap) {
        return redisTemplate.execute((RedisCallback<Map<String, String>>) connection -> {
            RedisClusterConnection clusterConnection = (RedisClusterConnection) connection;
            Map<String, String> resultMap = new HashMap<>();

            // 创建Pipeline对象
            clusterConnection.openPipeline();

            // 执行多个set命令
            for (Map.Entry<String, String> entry : keyValueMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                clusterConnection.set(key.getBytes(), value.getBytes());
            }

            // 执行所有命令并获取结果
            for (Map.Entry<String, String> entry : keyValueMap.entrySet()) {
                String key = entry.getKey();
                byte[] keyBytes = key.getBytes();
                byte[] valueBytes = (byte[]) clusterConnection.closePipeline().get(0);
                resultMap.put(new String(keyBytes), new String(valueBytes));
            }

            return resultMap;
        });
    }
}

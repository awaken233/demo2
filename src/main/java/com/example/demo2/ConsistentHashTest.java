package com.example.demo2;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.List;

public class ConsistentHashTest {
    public static final int NODE_SIZE = 10;
    public static final int STRING_COUNT = 100 * 100;
    private static ConsistentHash consistentHash = new ConsistentHash();
    private static List<String> sList = new ArrayList<>();

    public static void main(String[] args) {
        // 增加节点
        for (int i = 0; i < NODE_SIZE; i++) {
            String ip = "10.2.1." + i;
            consistentHash.addNode(ip);
        }

        // 生成需要缓存的数据;
        for (int i = 0; i < STRING_COUNT; i++) {
            sList.add(RandomStringUtils.randomAlphanumeric(10));
        }

        // 将数据放入到缓存中。
        for (String s : sList) {
            consistentHash.put(s, s);
        }

        for(int i = 0 ; i < 10 ; i ++) {
            int index = RandomUtils.nextInt(0, STRING_COUNT);
            String key = sList.get(index);
            String cache = (String) consistentHash.get(key);
            System.out.println("Random:"+index+",key:" + key + ",consistentHash get value:" + cache +",value is:" + key.equals(cache));
        }

        // 输出节点及数据分布情况
        for (Node node : consistentHash.nodeList){
            System.out.println(node);
        }

        // 新增一个数据节点
        consistentHash.addNode("10.2.1.110");
        for(int i = 0 ; i < 10 ; i ++) {
            int index = RandomUtils.nextInt(0, STRING_COUNT);
            String key = sList.get(index);
            String cache = (String) consistentHash.get(key);
            System.out.println("Random:"+index+",key:" + key + ",consistentHash get value:" + cache +",value is:" + key.equals(cache));
        }

        // 输出节点及数据分布情况
        for (Node node : consistentHash.nodeList){
            System.out.println(node);
        }
    }
}

package com.example.demo2;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wlei3
 * @since 2022/6/20 9:23
 */
@Slf4j
public class CommonTest {

    public static void main(String[] args) {
        Map<String, Integer> map1 = Maps.newHashMap();
        map1.put("a", 1);
        Map<String, Integer> map2 = Maps.newHashMap();
        map2.put("b", null);

        List<Map<String, Integer>> list = Lists.newArrayList(map1, map2);
        // flatMap
        Map<String, Integer> map = list.stream().map(Map::entrySet).flatMap(Collection::stream)
            .filter(entry -> entry.getValue() != null)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2));
        System.out.println(map);
    }
}

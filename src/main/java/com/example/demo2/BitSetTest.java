package com.example.demo2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * @author evtok
 * @since 2023/5/26 23:18
 */
public class BitSetTest {

    public static void main(String[] args) {
        List<Long> list = LongStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());

        Map<Long, List<Long>> map = new HashMap<>();
        for (Long aLong : list) {
            map.computeIfAbsent(aLong, k -> new ArrayList<>())
                    .add(aLong);
        }
        System.out.println(map);
    }
}





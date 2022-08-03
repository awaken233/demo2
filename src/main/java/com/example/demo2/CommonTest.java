package com.example.demo2;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;

/**
 * @author wlei3
 * @since 2022/6/20 9:23
 */
@Slf4j
public class CommonTest {

    public static void main(String[] args) {
        HashSet<String> positionBids = new HashSet<>();
        positionBids.add("1");
        positionBids.add("2");
        positionBids.retainAll(Sets.newHashSet("2"));
        System.out.println(positionBids);

    }
}

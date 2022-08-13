package com.example.demo2.cf;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CfTest3 {

    @Resource(name = "formDimissionPool")
    private Executors executors;


    public static void main(String[] args) {
        List<Integer> list = IntStream.range(0, 100).boxed().collect(Collectors.toList());
        CompletableFuture.runAsync(() -> {

        });
    }

    public static void doBussness(Integer ii) {
        // sleep(1000);
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(ii);
    }
}

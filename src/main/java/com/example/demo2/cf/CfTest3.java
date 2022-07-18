package com.example.demo2.cf;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CfTest3 {

    public static final Executor EXECUTOR = new ThreadPoolExecutor(10, 20, 60, TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(1000), new ThreadPoolExecutor.CallerRunsPolicy());

    public static void main(String[] args) {
        List<Integer> list = IntStream.range(0, 10).boxed().collect(Collectors.toList());
        list.forEach(i -> {
            CompletableFuture.runAsync(() -> {
                doBussness(i);
            }, EXECUTOR);
        });
    }

    public static void doBussness(Integer ii) {
        System.out.println(ii);
    }
}

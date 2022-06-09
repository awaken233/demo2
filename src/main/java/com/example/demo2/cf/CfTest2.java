package com.example.demo2.cf;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CfTest2 {

    public static final Executor EXECUTOR = new ThreadPoolExecutor(10, 20, 60, TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(1000), new ThreadPoolExecutor.CallerRunsPolicy());

    public static void main(String[] args) {
        List<Integer> list = IntStream.range(0, 10).boxed().collect(Collectors.toList());

        List<CompletableFuture<Integer>> collect = list.stream()
            .map(ii -> CompletableFuture.completedFuture(ii)
                .thenApplyAsync(CfTest2::doBussness, EXECUTOR)
                .handle(new DefaultValueHandle<>("orderService.getOrder", -1, ii))
            ).collect(Collectors.toList());
        CompletableFuture<List<Integer>> sequence = FutureUtils.sequence(collect);
        List<Integer> join = sequence.join();
        System.out.println(join);
    }

    public static Integer doBussness(Integer ii) {
        if (ii == 5) {
            throw new NullPointerException();
        }
        return ii * 2;
    }
}

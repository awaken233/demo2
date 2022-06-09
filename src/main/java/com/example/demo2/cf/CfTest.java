package com.example.demo2.cf;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author wlei3
 * @since 2022/5/30 9:04
 */
public class CfTest {

    public static final Executor EXECUTOR = new ThreadPoolExecutor(10, 20, 60, TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(1000), new ThreadPoolExecutor.CallerRunsPolicy());

    public static void main(String[] args) {
        List<Integer> list = IntStream.range(0, 1000000).boxed().collect(Collectors.toList());
        List<List<Integer>> partition = Lists.partition(list, 1000);
        List<CompletableFuture<List<Integer>>> collectCf = partition.stream()
            .map(CompletableFuture::completedFuture)
            .map(future -> future.thenApplyAsync(CfTest::doBussness, EXECUTOR))
            .collect(Collectors.toList());
        List<Integer> join = FutureUtils.sequenceList(collectCf).join();
        System.out.println(join);
    }

    public static List<Integer> doBussness(List<Integer> list) {
        return list.stream().map(i -> i * 2).collect(Collectors.toList());
    }
}

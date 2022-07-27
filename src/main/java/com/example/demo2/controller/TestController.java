package com.example.demo2.controller;

import com.example.demo2.cf.FutureUtils;
import com.example.demo2.component.PrivatizeThreadPoolUtils;
import com.example.demo2.config.EntryDemo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author wlei3
 * @since 2022/4/25 17:50
 */
@Slf4j
@RestController
public class TestController {

    @Autowired
    private PrivatizeThreadPoolUtils privatizeThreadPoolUtils;

    @PostMapping("/test")
    public ConcurrentHashMap<Integer, Integer> test() {
        List<Integer> list = IntStream.range(0, 100).boxed().collect(Collectors.toList());

        ConcurrentHashMap<Integer, Integer> result = new ConcurrentHashMap<>();
        List<CompletableFuture<Integer>> cfs = list.stream().map(i -> {
            return CompletableFuture.supplyAsync(() -> {
                Integer re = doBussness(i);
                result.put(i, re);
                return re;
            }, privatizeThreadPoolUtils.getThreadPool());
        }).collect(Collectors.toList());
        FutureUtils.sequence(cfs).join();

        return result;
    }


    public static final Executor EXECUTOR = new ThreadPoolExecutor(10, 20, 60, TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(1000), new ThreadPoolExecutor.CallerRunsPolicy());

    public static Integer doBussness(Integer ii) {
        // sleep(1000);
        try {
            TimeUnit.MICROSECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(ii);
        return ii * 2;
    }


    //最大可用的CPU核数
    public static final int PROCESSORS = Runtime.getRuntime().availableProcessors() + 1;
    //线程最大的空闲存活时间，单位为秒
    public static final int KEEPALIVETIME = 60 * 2;
    //任务缓存队列长度
    public static final int BLOCKINGQUEUE_LENGTH = 2000;

    public static ThreadPoolExecutor getThreadPool() {
        return new ThreadPoolExecutor(
            PROCESSORS * 2,
            PROCESSORS * 10,
            KEEPALIVETIME,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(BLOCKINGQUEUE_LENGTH),
            new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public static void main(String[] args) {
        EntryDemo entryDemo = new EntryDemo(1, "Active");
        EntryDemo entryDemo2 = new EntryDemo(1, "Leaving");
        List<EntryDemo> list = Lists.newArrayList(entryDemo, entryDemo2);
        Map<Integer, String> map = list.stream()
            .collect(Collectors.toMap(EntryDemo::getId, EntryDemo::getHiringStatus,
                TestController::getOperator));
        System.out.println(map);
    }

    private static <T> T getOperator(T key1, T key2) {
        return key1;
    }

    private static <T> BinaryOperator<T> getOperator() {
        return (key1, key2) -> key1;
    }
}

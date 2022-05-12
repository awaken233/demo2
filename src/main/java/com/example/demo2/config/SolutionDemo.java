package com.example.demo2.config;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author wlei3
 * @since 2022/3/30 16:54
 */
public class SolutionDemo {

    //根据业务定义线程池
    private static ExecutorService executor = new ThreadPoolExecutor(50, 100, 10, TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(200));

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        long time1 = System.currentTimeMillis();

        List<CompletableFuture<Integer>> list = IntStream.range(0, 100).boxed()
            .map(CompletableFuture::completedFuture)
            .map(future -> future.thenApplyAsync(SolutionDemo::doBussness, executor))
            .collect(Collectors.toList());

        //等待所以业务处理完毕
        CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
        System.out.println("总耗时" + (System.currentTimeMillis() - time1));

        //输出所有处理结果
        for (CompletableFuture<Integer> completableFuture : list) {
            System.out.println(completableFuture.get());
        }
    }

    private static int doBussness(int i) {
        //模拟处理耗时3s
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return i;
    }

}

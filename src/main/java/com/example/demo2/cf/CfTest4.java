package com.example.demo2.cf;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CfTest4 {
    // 中断的次数
    private static int INTERRUPT_TIMES = 0;
    public static final ExecutorService EXECUTOR = new ThreadPoolExecutor(20, 20, 60,
        TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(20), new ThreadPoolExecutor.CallerRunsPolicy());

    public static void main(String[] args) throws Exception {
        // 循环两次 extracted()
        for (int i = 0; i < 100; i++) {
            extracted();
            INTERRUPT_TIMES = 0;
        }

    }

    private static void extracted() throws InterruptedException, ExecutionException {
        List<Callable<Integer>> list = IntStream.range(0, 10).boxed()
            .map(i -> (Callable<Integer>) () -> doBussness(i)).collect(Collectors.toList());
        // 统计耗时
        long start = System.currentTimeMillis();
        List<Future<Integer>> futures = list.stream().map(EXECUTOR::submit)
            .collect(Collectors.toList());
        // 判断 futures 任意一个 isDone
        Future<Integer> fastFailFuture = getFirstCompleteFuture(futures);
        futures.stream().filter(f -> f != fastFailFuture).forEach(f -> {
            boolean cancel = f.cancel(true);
            if (cancel) {
                ++INTERRUPT_TIMES;
            }
        });

        System.out.println("耗时2->>>>>>>" + (System.currentTimeMillis() - start));
        // 打印中断次数
        System.out.println("中断次数->>>>>>>" + INTERRUPT_TIMES);
        // 打印 fastFailFuture 的结果
        System.out.println("最先执行完的结果->>>>>>>" + fastFailFuture.get());
    }

    public static <V> Future<V> getFirstCompleteFuture(Collection<Future<V>> futures)
        throws InterruptedException {
        while (true) {
            for (Future<V> future : futures) {
                if (future.isDone()) {
                    return future;
                }
            }
            Thread.sleep(100);
        }
    }

    public static Integer doBussness(Integer ii) {
        try {
            if (ii == 5) {
                TimeUnit.SECONDS.sleep(1);
            } else {
                TimeUnit.SECONDS.sleep(3);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return ii;
    }
}

package com.example.demo2.cf;

import cn.hutool.core.date.StopWatch;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CfTest3 {

    public static void main(String[] args) throws InterruptedException {
        List<Integer> list = IntStream.range(0, 10).boxed().collect(Collectors.toList());

        List<CompletableFuture<Integer>> cfs = list.stream()
            .map(i -> CompletableFuture.supplyAsync(() -> {
                return doBussness(i);
            }).handle((integer, throwable) -> {
                if (throwable != null) {
                    return -1;
                }
                return integer;
            })).collect(Collectors.toList());

        CompletableFuture<List<Integer>> sequence1 = FutureUtils.sequence(cfs);


        // 再构造一个当有一个失败则失败的的CompletableFuture
        CompletableFuture<Integer> anyException = new CompletableFuture<>();
        cfs.forEach(cf -> cf.handle((r, e) -> {
            if (r == 3)
                return anyException.complete(r);
            return r;
        }));
        // 让allComplete和anyException其中有一个完成则完成
        // 如果allComplete有一个异常，anyException会成功完成，则整个就提前完成了
        StopWatch sw2 = new StopWatch();
        sw2.start();
        CompletableFuture<Object> sequence2 = CompletableFuture.anyOf(sequence1,
            anyException);
        Object join = sequence2.join();
        System.out.println(join);
//        System.out.println(sequence1.join());
        sw2.stop();
        System.out.println("sw2=======>"+sw2.getTotalTimeMillis());
    }

    public static Integer doBussness(Integer ii) {

        try {
            if (ii == 5) {
                TimeUnit.SECONDS.sleep(4);
            } else if (ii == 3) {
                throw new NullPointerException();
            } else {
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ii;
    }
}

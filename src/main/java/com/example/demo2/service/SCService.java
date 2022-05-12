package com.example.demo2.service;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo2.dao.SCMapper;
import com.example.demo2.entity.SC;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author wlei3
 */
@Slf4j
@Service
public class SCService extends ServiceImpl<SCMapper, SC> {

    final ExecutorService pool = new ThreadPoolExecutor(12, 24, 60L, TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(2000),
        new ThreadFactoryBuilder().setNameFormat("demo-pool-%d").build(),
        new ThreadPoolExecutor.AbortPolicy());

    public void test() {
        IntStream.range(0, 700).mapToObj(i -> Stream.generate(new SCSupplier())
            .limit(1000).collect(Collectors.toList())).forEach(this::saveBatch);
    }


    private static class SCSupplier implements Supplier<SC> {
        private final AtomicInteger idx = new AtomicInteger(0);

        @Override
        public SC get() {
            SC sc = new SC();
            sc.setCId(RandomUtil.randomInt(0, 100));
            sc.setSId(RandomUtil.randomInt(0, 70000));
            sc.setScId(idx.getAndIncrement());
            sc.setScore(RandomUtil.randomInt(0, 100));
            return sc;
        }
    }
}


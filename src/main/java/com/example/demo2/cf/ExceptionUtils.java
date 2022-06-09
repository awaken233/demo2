package com.example.demo2.cf;

import cn.hutool.core.util.ObjectUtil;

import java.util.Collections;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

public class ExceptionUtils {

    /**
     * 提取真正的异常
     */
    public static Throwable extractRealException(Throwable throwable) {
        if (throwable instanceof CompletionException || throwable instanceof ExecutionException) {
            if (throwable.getCause() != null) {
                return throwable.getCause();
            }
        }
        return throwable;
    }

    public static void main(String[] args) {
        if (ObjectUtil.isEmpty(Collections.singletonList(null))) {
            System.out.println(1);
        }
        System.out.println(2);

    }
}
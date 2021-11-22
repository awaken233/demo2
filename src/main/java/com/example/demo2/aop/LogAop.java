package com.example.demo2.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Method;

/**
 * @author wlei3
 */
@Slf4j
@Aspect
public class LogAop {

    @Value("${com.example.url}")
    private String url;

    @Around("execution(* com.example.demo2.controller..*.*(..))")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object ret = joinPoint.proceed();
        //执行完目标方法之后打印
        log.info("after execute method:" + method.getName());
        return ret;
    }
}
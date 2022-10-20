package com.example.demo2.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class FeignCostTimeAop {

    public FeignCostTimeAop() {
    }

    //路径就自己定义咯
    @Pointcut("execution(public * com.example.demo2.feign..*(..))")
    public void feignInterfacePointCut() {
    }

    @Around(value = "feignInterfacePointCut()")
    public Object aroundFeignInterfaceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object obj = null;
        try {
            obj = joinPoint.proceed();
        } finally {
            log.info("method={},cost={}ms", joinPoint.getSignature().getName(),
                System.currentTimeMillis() - start);
        }
        return obj;
    }
}

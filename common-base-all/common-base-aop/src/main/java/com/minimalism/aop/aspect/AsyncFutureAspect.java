package com.minimalism.aop.aspect;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @Author yan
 * @Date 2024/11/3 上午2:54:17
 * @Description
 */
@Aspect
@Slf4j
@Component
public class AsyncFutureAspect implements AbstractAsyncFutureAspect {


    /**
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Override
    @Around(value = "SysLog()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        return AbstractAsyncFutureAspect.super.around(joinPoint);
    }


    @SneakyThrows
    public static void main(String[] args) {
        leb:
        for (int i = 0; i < 5; i++) {
            for (int k = 0; k < 2; k++) {
                System.out.println("i=" + i + " k=" + k);
                if (k == 0) {
                    break leb;
                }
            }
        }
    }


}

package com.minimalism.aop.all.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @Author yan
 * @Date 2024/11/12 上午1:32:53
 * @Description
 */
@Aspect
@Slf4j
@Component
public class AviatorValidAspect implements AbsAviatorValidAspect {


    /**
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Override
    @Around(value = "Aop()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        check(joinPoint);
        return AbsAviatorValidAspect.super.around(joinPoint);
    }



}

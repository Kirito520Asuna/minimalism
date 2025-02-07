package com.minimalism.aop.aspect;

import com.googlecode.aviator.AviatorEvaluator;
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
public class AviatorValidAspect implements AbstractAviatorValidAspect {


    /**
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Override
    @Around(value = "SysLog()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        check(joinPoint);
        return AbstractAviatorValidAspect.super.around(joinPoint);
    }



}

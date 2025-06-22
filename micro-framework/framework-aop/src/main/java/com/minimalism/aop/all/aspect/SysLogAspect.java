package com.minimalism.aop.all.aspect;

import com.minimalism.aop.abs.aspect.AbsSysLog;
import com.minimalism.aop.utils.thread.AopThreadMdcUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @Author yan
 * @Date 2025/5/21 23:42:27
 * @Description
 */
@Slf4j
@Getter
@Aspect
@Component
public class SysLogAspect implements AbsSysLog {

    @Override
    @Around(value = "Aop()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return AbsSysLog.super.aroundSysLog(joinPoint);
        }finally {
            AopThreadMdcUtil.clear();
        }
    }

}

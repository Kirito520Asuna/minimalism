package com.minimalism.aop.all.aspect;

import com.minimalism.aop.abs.aspect.AbsSysLog;
import com.minimalism.aop.utils.thread.AopThreadMdcUtil;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;


/**
 * @author yan
 * @date 2023/4/12 0012 18:31
 */
@Aspect
@Slf4j
@Component
@Getter
public class SysLogAspect implements AbsSysLog {


    @Override
    @Around(value = "Aop()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return aroundSysLog(joinPoint);
        } finally {
            AopThreadMdcUtil.clear();
        }
    }

}

package com.minimalism.abstractinterface.aop;

import com.minimalism.aop.log.SysLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @Author yan
 * @Date 2023/6/1 0001 17:03
 * @Description
 */
public interface AbstractSysLog extends AbstractAop {
    default SysLog getAnnotationLog(JoinPoint joinPoint) {
        return getAnnotation(joinPoint, SysLog.class);
    }
    @Override
    @Pointcut(value = "@annotation(com.minimalism.aop.log.SysLog)")
    default void Aop() {
    }

}

package com.minimalism.aop.aspect;

import com.minimalism.abstractinterface.aop.AbstractAop;
import com.minimalism.abstractinterface.aop.AbstractSysLog;
import com.minimalism.aop.AopConstants;
import com.minimalism.aop.security.Login;
import com.minimalism.enums.ApiCode;
import com.minimalism.exception.GlobalCustomException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @Author yan
 * @Date 2024/9/27 上午10:49:44
 * @Description
 */

public interface AbstractLoginAspect extends AbstractAop {

    @Override
    default int getOrder() {
        return AopConstants.LoginOrder;
    }

    @Override
    @Pointcut(value = "@annotation(com.minimalism.aop.security.Login)")
    default void Aop() {
    }

    @Override
    @Around(value = "Aop()")
    default Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        log();
        // 注解鉴权
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        checkMethodAnnotation(signature.getMethod());
        try {
            // 执行原有逻辑
            return AbstractAop.super.around(joinPoint);
        } catch (Throwable e) {
            throw e;
        }
    }

    /**
     * 对一个Method对象进行注解检查
     */
    default void checkMethodAnnotation(Method method) {
        // 校验 @RequiresLogin 注解
        Login login = method.getAnnotation(Login.class);
        if (login != null) {
            checkLogin();
        }
    }

    /**
     * 检查登陆
     */
    default void checkLogin() {
    }
}

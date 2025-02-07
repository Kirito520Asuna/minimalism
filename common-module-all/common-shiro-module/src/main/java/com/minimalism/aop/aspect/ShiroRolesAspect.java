package com.minimalism.aop.aspect;

import com.minimalism.abstractinterface.AbstractShiroAopAspect;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @Author yan
 * @Date 2024/10/21 上午1:05:53
 * @Description
 */
@Aspect
@Slf4j
@Component
public class ShiroRolesAspect implements AbstractShiroAopAspect {
    @Pointcut(value = "@annotation(com.minimalism.aop.shiro.ShiroRoles)")
    @Override
    public void SysLog() {
    }


    //@Override
    //public <T extends Annotation> List<String> getKeyList(T shiroRolesOrPermission) {
    //    return AbstractShiroAopAspect.super.getKeyList(shiroRolesOrPermission);
    //}
    //
    //@Around(value = "SysLog()")
    //@Override
    //public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    //    hasRoles(joinPoint);
    //    return AbstractShiroAopAspect.super.around(joinPoint);
    //}
}

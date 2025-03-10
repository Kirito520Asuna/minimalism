package com.minimalism.aop.aspect;

import com.minimalism.abstractinterface.AbstractShiroAopAspect;
import com.minimalism.utils.shiro.SecurityContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author yan
 * @Date 2024/10/21 上午1:05:53
 * @Description
 */
@Aspect
@Slf4j
@Component
public class ShiroAopAspect implements AbstractShiroAopAspect {
    @Override
    @Pointcut(value = "ShiroPermissionsAspect.SysLog()||ShiroRolesAspect.SysLog()")
    public void Aop() {
    }

    @Override
    public List<String> getRoles() {
        return SecurityContextUtil.getRoles();
    }

    @Override
    public List<String> getPermissions() {
        return SecurityContextUtil.getPermissions();
    }

    @Around(value = "Aop()")
    @Override
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        hasRolesPermissions(joinPoint);
        return AbstractShiroAopAspect.super.around(joinPoint);
    }
}

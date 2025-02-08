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
public class ShiroPermissionsAspect implements AbstractShiroAopAspect {
    @Pointcut(value = "@annotation(com.minimalism.aop.shiro.ShiroPermissions)")
    @Override
    public void SysLog() {
    }
}

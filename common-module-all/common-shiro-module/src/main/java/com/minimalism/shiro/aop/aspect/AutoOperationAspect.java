package com.minimalism.shiro.aop.aspect;

import com.minimalism.aop.abs.aspect.AbsAutoOperation;
import com.minimalism.shiro.utils.SecurityContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @Author yan
 * @Date 2024/10/3 下午4:49:30
 * @Description
 */
@Aspect
@Slf4j
@Component
public class AutoOperationAspect implements AbsAutoOperation {
    @Override
    public String getOperator() {
        return SecurityContextUtil.getUserIdNoThrow();
    }


}

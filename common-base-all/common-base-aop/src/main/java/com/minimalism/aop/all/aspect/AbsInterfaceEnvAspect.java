package com.minimalism.aop.all.aspect;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.aop.abs.aop.AbsAop;
import com.minimalism.aop.all.AopConstants;
import com.minimalism.aop.all.env.InterfaceEnv;
import com.minimalism.base.exception.GlobalCustomException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface AbsInterfaceEnvAspect extends AbsAop {
    @Override
    default int getOrder() {
        return AopConstants.EnvOrder;
    }

    @Override
    @Pointcut(value = "@annotation(com.minimalism.aop.all.env.InterfaceEnv)")
    default void Aop() {
    }


    @Override
    @Around(value = "Aop()")
    default Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        log();
        List<String> valueList = CollUtil.newArrayList();
        InterfaceEnv annotation = getAnnotation(joinPoint, InterfaceEnv.class);
        String value = annotation.value();
        String[] values = annotation.values();
        boolean ignore = annotation.ignore();

        valueList.addAll(Arrays.stream(values).collect(Collectors.toList()));
        valueList.add(value);

        String active = SpringUtil.getBean(Environment.class).getProperty("spring.profiles.active", "default");
        //满足  环境
        boolean condition = valueList.contains(active);
        //进一步确定条件
        condition = ignore ? !condition : condition;
        if (!condition) {
            error("当前环境为{},不满足{}环境", active, valueList.toString());
            throw new GlobalCustomException("当前环境 接口禁用");
        }
        try {
            // 执行原有逻辑
            return AbsAop.super.around(joinPoint);
        } catch (Throwable e) {
            throw e;
        }
    }
}

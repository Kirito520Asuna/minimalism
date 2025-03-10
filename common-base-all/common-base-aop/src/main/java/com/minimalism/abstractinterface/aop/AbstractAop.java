package com.minimalism.abstractinterface.aop;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONConfig;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.aop.log.SysLog;
import com.minimalism.utils.object.ObjectUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * @Author yan
 * @Date 2023/6/1 0001 17:03
 * @Description
 */
public interface AbstractAop extends AbstractBean {
    JSONConfig jsonConfig = JSONConfig.create().setIgnoreNullValue(false);

    @Override
    @PostConstruct
    default void init() {
        debug("[init]-[Aop] {}", getClass().getName());
    }

    @SneakyThrows
    default <T extends Annotation> T getAnnotation(JoinPoint joinPoint, Class<T> annotationClass) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        T annotation = null;
        if (ObjectUtil.isNotEmpty(method)) {
            annotation = method.getAnnotation(annotationClass);
        }
        return annotation;
    }

    default String getUserAgent(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        return ObjectUtils.defaultIfEmpty(ua, StrUtil.EMPTY);
    }

    default String getModule(JoinPoint joinPoint) {
        Tag tag = getAnnotation(joinPoint, Tag.class);
        String module = "";
        if (ObjectUtils.isNotEmpty(tag)) {
            if (StrUtil.isNotEmpty(tag.name())) {
                // 优先读取 @Tag 的 name 属性
                module = tag.name();
            } else if (StrUtil.isNotEmpty(tag.description())) {
                // 没有的话，读取 @API 的 description 属性
                module = tag.description();
            }
        }

        if (StrUtil.isBlank(module)) {
            Api api = getAnnotation(joinPoint, Api.class);
            if (ObjectUtils.isNotEmpty(api)) {
                if (StrUtil.isNotEmpty(api.value())) {
                    // 优先读取 @Api 的 value 属性
                    module = api.value();
                } else if (StrUtil.isNotEmpty(api.description())) {
                    // 没有的话，读取 @Api 的 description 属性
                    module = api.description();
                }
            }
        }
        return module;
    }

    default String getDescription(JoinPoint joinPoint) {
        Operation operation = getAnnotation(joinPoint, Operation.class);
        String description = "";
        if (ObjectUtils.isNotEmpty(operation)) {
            if (StrUtil.isNotEmpty(operation.summary())) {
                // 优先读取 @Operation 的 summary 属性
                description = operation.summary();
            } else if (StrUtil.isNotEmpty(operation.description())) {
                // 没有的话，读取 @Operation 的 description 属性
                description = operation.description();
            }
        }

        if (StrUtil.isBlank(description)) {
            ApiOperation apiOperation = getAnnotation(joinPoint, ApiOperation.class);
            if (ObjectUtils.isNotEmpty(apiOperation)) {
                if (StrUtil.isNotEmpty(apiOperation.value())) {
                    // 优先读取 @ApiOperation 的 value 属性
                    description = apiOperation.value();
                } else if (StrUtil.isNotEmpty(apiOperation.notes())) {
                    // 没有的话，读取 @Api 的 notes 属性
                    description = apiOperation.notes();
                }
            }
        }
        return description;
    }

    void Aop();

    default void doBefore(JoinPoint joinPoint) throws Exception {
        info("[doBefore] {}",getClass().getName());
    }

    default Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }

    default void doAfterReturning(JoinPoint joinPoint, Object reValue) {
        info("[doAfterReturning] {}",getClass().getName());
    }

}

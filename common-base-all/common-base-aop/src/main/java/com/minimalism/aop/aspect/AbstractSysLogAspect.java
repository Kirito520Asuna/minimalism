package com.minimalism.aop.aspect;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.minimalism.abstractinterface.aop.AbstractSysLog;
import com.minimalism.aop.log.SysLog;
import com.minimalism.enums.RequestMethod;

import com.minimalism.utils.gateway.GatewayUtils;
import com.minimalism.utils.object.ObjectUtils;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yan
 * @date 2023/4/12 0012 18:31
 */
@Aspect
@Slf4j
@Component
@Getter
public class AbstractSysLogAspect implements AbstractSysLog {
    private final static JSONConfig jsonConfig = JSONConfig.create().setIgnoreNullValue(false);

    @Resource
    private Environment environment;

    /**
     * 需要获取请求行的请求方法
     */
    private final static List<String> getParamsMethod = Stream.
            of(RequestMethod.GET.name(), RequestMethod.DELETE.name()).collect(Collectors.toList());
    /**
     * 需要获取请求体的请求方法
     */
    private final static List<String> getBodyMethod = Stream.
            of(RequestMethod.POST.name(), RequestMethod.PUT.name()).collect(Collectors.toList());


    public static <T extends Annotation> T getClassAnnotation(JoinPoint joinPoint, Class<T> annotationClass) {
        return ((MethodSignature) joinPoint.getSignature()).getMethod().getDeclaringClass().getAnnotation(annotationClass);
    }


    /**
     * 是否存在注解，如果存在就获取
     */
    @Override
    public SysLog getAnnotationLog(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if (ObjectUtils.isNotEmpty(method)) {
            return method.getAnnotation(SysLog.class);
        }
        return null;
    }

    @Override
    @Pointcut(value = "@annotation(com.minimalism.aop.log.SysLog)")
    public void SysLog() {
    }


    @Override
    @Around(value = "SysLog()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取是否有注解
        SysLog sysLog = getAnnotationLog(joinPoint);
        /**
         * 开启请求日志
         */
        boolean openRequestLog = ObjectUtils.isNotEmpty(sysLog) && sysLog.flag();
        if (openRequestLog) {
            // 接收到请求，记录请求内容
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            //可能有空指针问题

            HttpServletRequest request = attributes.getRequest();
            // 记录下请求内容、
            String applicationName = environment.getProperty("spring.application.name", String.class);

            String method = request.getMethod();

            Map<String, String> paramMap = ServletUtil.getParamMap(request);
            log.debug("parse:{}", JSONUtil.parse(paramMap, jsonConfig));

            String title = sysLog.title();
            String remoteAddr = request.getRemoteAddr();
            StringBuffer requestURL = request.getRequestURL();

            Object[] pointArgs = joinPoint.getArgs();
            String args = JSONUtil.parse(CollUtil.isEmpty(paramMap) ? pointArgs : paramMap, jsonConfig).toString();

            String declaringTypeName = joinPoint.getSignature().getDeclaringTypeName();
            String name = joinPoint.getSignature().getName();
            String module = getModule(joinPoint);
            if (StrUtil.isBlank(title)) {
                title = getDescription(joinPoint);
            }

            String url = GatewayUtils.replaceUrl(request, requestURL.toString());

            log.info(new StringBuffer()
                            .append("\n====================================请求内容====================================")
                            .append("\n请求服务名 : {}")
                            .append("\n请求模块名 : {}")
                            .append("\n请求描述 : {}")
                            .append("\n请求IP : {}")
                            .append("\n请求地址 : {}")
                            .append("\n请求方式 : {}")
                            .append("\n请求参数 : {}")
                            .append("\n请求类方法 : {}.{}")
                            .append("\n================================================================================")
                            .toString()
                    , applicationName, module, title, remoteAddr, url, method, args, declaringTypeName, name);
        }
        // 执行方法
        Object around = AbstractSysLog.super.around(joinPoint);

        /**
         * 开启响应日志
         */
        boolean openResultLog = ObjectUtils.isNotEmpty(sysLog) && sysLog.result();
        Object returnObj = around;
        if (openResultLog) {
            // 处理完请求，返回内容
            if (returnObj == null) {
                returnObj = "返回值为空";
            } else if (JSONUtil.isTypeJSON(JSONUtil.toJsonStr(returnObj))) {
                JSON parse = JSONUtil.parse(around, jsonConfig);
                returnObj = parse;
            } else {
                returnObj = around;
            }
            log.info(new StringBuffer()
                    .append("\n====================================响应内容====================================")
                    .append("\n响应 : {}")
                    .append("\n================================================================================")
                    .toString(), JSONUtil.toJsonStr(returnObj));
        }

        return around;
    }

}

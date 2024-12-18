package com.minimalism.aop.aspect;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.minimalism.abstractinterface.aop.AbstractSysLog;
import com.minimalism.aop.log.SysLog;
import com.minimalism.enums.RequestMethod;

import com.minimalism.pojo.OperateLogInfo;
import com.minimalism.result.Result;
import com.minimalism.abstractinterface.service.AbstractOperateLogService;
import com.minimalism.utils.object.ObjectUtils;
import com.minimalism.utils.servlet.ServletUtils;
import com.minimalism.utils.date.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author yan
 * @Date 2024/5/15 0015 10:13
 * @Description
 */
@Aspect
@Slf4j
@Component
public class OperateLogAspectAbstract implements AbstractSysLog {
    @Lazy
    @Resource
    private AbstractSysLogAspect sysLogAspect;
    @Lazy
    @Resource
    private Environment env;
    @Value("${operate.log.user.type.name:user_id}")
    private String userTypeName;
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
    /**
     * 操作记录通用实体
     */
    private ThreadLocal<OperateLogInfo> operateLogThreadLocal = new TransmittableThreadLocal<>();

    /**
     * 获取
     *
     * @param now
     * @return
     */
    public OperateLogInfo getOperateLog(LocalDateTime now) {
        OperateLogInfo operateLog = operateLogThreadLocal.get();
        if (operateLog == null && ObjectUtil.isNotEmpty(now)) {
            //获取秒数
            //Long second = now.toEpochSecond(ZoneOffset.of("+8"));
            //获取毫秒数 自动获取时区
            Long milliSecond = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            String traceId = new StringBuffer().append(UUID.randomUUID()).append("::").append(milliSecond).toString();
            operateLog = new OperateLogInfo();
            operateLog.setTraceId(traceId);
            setOperateLog(operateLog);
        }
        return operateLog;
    }

    public OperateLogInfo setOperateLog(OperateLogInfo operateLog) {
        operateLogThreadLocal.set(operateLog);
        return operateLog;
    }

    @Override
    @Pointcut("AbstractSysLogAspect.SysLog()")
    public void SysLog() {
//        sysLogAspect.SysLog();
    }

    @Override
    public SysLog getAnnotationLog(JoinPoint joinPoint) {
        return sysLogAspect.getAnnotationLog(joinPoint);
    }

    @SneakyThrows
    @Override
    //@Before(value = "SysLog()")
    public void doBefore(JoinPoint joinPoint) {
        LocalDateTime now = DateUtils.longToLocalDateTime(System.currentTimeMillis());
        OperateLogInfo operateLog = getOperateLog(now);
        log.debug("|TRACE_ID:{}|", operateLog == null ? null : operateLog.getTraceId());
        try {
            SysLog sysLog = getAnnotationLog(joinPoint);
            if (sysLog.flag() && sysLog.enableOperate()) {
                // 接收到请求，记录请求内容
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                //可能有空指针问题
                HttpServletRequest request = attributes.getRequest();
                // 记录下请求内容、
                String applicationName = env.getProperty("spring.application.name", String.class);
                Map<String, String> paramMap = ServletUtil.getParamMap(request);
                Object[] pointArgs = joinPoint.getArgs();
                String args = Arrays.toString(pointArgs);
                args = CollUtil.isEmpty(paramMap) ? args : JSONUtil.parse(paramMap, jsonConfig).toString();

                String declaringTypeName = joinPoint.getSignature().getDeclaringTypeName();
                String name = joinPoint.getSignature().getName();


                Operate operate = new Operate()
                        .setNow(now)
                        .setArgs(args)
                        .setApplicationName(applicationName)
                        .setMethod(request.getMethod())
                        .setParamMap(paramMap)
                        .setTitle(sysLog.title())
                        .setRemoteAddr(ServletUtil.getClientIP(request))
                        .setRequestURL(request.getRequestURL())
                        .setPointArgs(pointArgs)
                        .setDeclaringTypeName(declaringTypeName)
                        .setInterfaceName(name);
                operateInit(joinPoint, operateLog, sysLog, request, operate);
                log.debug("start::syslog:id:{}", operateLog == null ? null : operateLog.getTraceId());//
            }
        } finally {
//            sysLogAspect.doBefore(joinPoint);
            setOperateLog(operateLog);
        }

    }

    @Override
    @Around(value = "SysLog()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        LocalDateTime now = DateUtils.longToLocalDateTime(System.currentTimeMillis());
        OperateLogInfo operateLog = getOperateLog(now);
        log.debug("|TRACE_ID:{}|", operateLog == null ? null : operateLog.getTraceId());
        SysLog sysLog = getAnnotationLog(joinPoint);

        boolean hasSysLog = ObjectUtils.isNotEmpty(sysLog);
        if (hasSysLog && sysLog.enableOperate()) {
            // 接收到请求，记录请求内容
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            //可能有空指针问题
            HttpServletRequest request = attributes.getRequest();
            // 记录下请求内容、
            String applicationName = env.getProperty("spring.application.name", String.class);
            Map<String, String> paramMap = ServletUtil.getParamMap(request);
            Object[] pointArgs = joinPoint.getArgs();
            String args = Arrays.toString(pointArgs);
            args = CollUtil.isEmpty(paramMap) ? args : JSONUtil.parse(paramMap, jsonConfig).toString();

            String declaringTypeName = joinPoint.getSignature().getDeclaringTypeName();
            String name = joinPoint.getSignature().getName();


            Operate operate = new Operate()
                    .setNow(now)
                    .setArgs(args)
                    .setApplicationName(applicationName)
                    .setMethod(request.getMethod())
                    .setParamMap(paramMap)
                    .setTitle(sysLog.title())
                    .setRemoteAddr(ServletUtil.getClientIP(request))
                    .setRequestURL(request.getRequestURL())
                    .setPointArgs(pointArgs)
                    .setDeclaringTypeName(declaringTypeName)
                    .setInterfaceName(name);
            operateInit(joinPoint, operateLog, sysLog, request, operate);
        }

        Object around = AbstractSysLog.super.around(joinPoint);

        if (hasSysLog && sysLog.logResultData()) {
            //记录响应
            JSON parse = JSONUtil.parse(around != null ? around : new LinkedHashMap<>(), jsonConfig);
            Result bean = parse.toBean(Result.class);
            operateFinish(operateLog, bean);
        }

        return around;
    }

    @SneakyThrows
    @Override
    //@AfterReturning(returning = "reValue", pointcut = "SysLog()")
    public void doAfterReturning(JoinPoint joinPoint, Object reValue) {
        OperateLogInfo operateLog = getOperateLog(null);
        log.debug("|TRACE_ID:{}|", operateLog == null ? null : operateLog.getTraceId());
        try {
            SysLog sysLog = getAnnotationLog(joinPoint);
            if (sysLog.enableOperate() && sysLog.logResultData()) {
                //记录响应
                JSON parse = JSONUtil.parse(reValue != null ? reValue : new LinkedHashMap<>(), jsonConfig);
                Result bean = parse.toBean(Result.class);
                operateFinish(operateLog, bean);
            }
        } finally {
            setOperateLog(null);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    class Operate {
        /**
         * 应用名
         */
        String applicationName;
        /**
         * 请求方法
         */
        String method;
        /**
         * 请求参数
         */
        Map<String, String> paramMap;
        /**
         * 请求描述
         */
        String title;
        /**
         * 请求IP
         */
        String remoteAddr;
        /**
         * 请求地址
         */
        StringBuffer requestURL;
        /**
         * 请求参数
         */
        Object[] pointArgs;
        /**
         * 请求参数
         */
        String args;
        /**
         * 类名
         */
        String declaringTypeName;
        /**
         * 方法名
         */
        String interfaceName;
        /**
         * 当前时间
         */
        LocalDateTime now;
    }

    /**
     * 初始化操作记录
     *
     * @param joinPoint
     * @param operateLog
     * @param sysLog
     * @param request
     * @param operate
     * @return
     * @throws IOException
     */
    public OperateLogInfo operateInit(JoinPoint joinPoint, OperateLogInfo operateLog, SysLog sysLog, HttpServletRequest request, Operate operate) throws IOException {
        String title = operate.getTitle();
        String remoteAddr = operate.getRemoteAddr();
        StringBuffer requestURL = operate.getRequestURL();
        Object[] pointArgs = operate.getPointArgs();
        String args = operate.getArgs();
        String declaringTypeName = operate.getDeclaringTypeName();
        String interfaceName = operate.getInterfaceName();
        LocalDateTime now = operate.getNow();
        String applicationName = operate.getApplicationName();
        String method = operate.getMethod();
        Map<String, String> paramMap = operate.getParamMap();


        String module = sysLog.module();
        // todo 尝试读取 {@link Tag#interfaceName()} 属性
        module = ObjectUtil.isEmpty(module) ? getModule(joinPoint) : module;
        //获取用户类型名
        //String userTypeName = environment.getProperty("operate.log.user.type.interfaceName");
        String userTypeName = this.userTypeName;
        String userType = null;
        if (ObjectUtil.isNotEmpty(userTypeName)) {
            //从请求头中获取用户类型 可指定枚举用于记录服务器之间的调用记录
            userType = request.getHeader(userTypeName);
        }
        // 获取浏览器
        String userAgent = getUserAgent(request);
        operateLog.setName(title)
                .setContent(title)
                .setStartTime(DateUtils.LocalDateTimeTolong(now))
                .setRequestApplication(applicationName)
                .setModule(module)
                .setUserType(userType)
                .setUserAgent(userAgent)
                .setType(sysLog.type().name())
                .setBusinessType(sysLog.businessType().name())
                .setRequestUrl(requestURL.toString())
                .setRequestMethod(method)
                .setUserIp(remoteAddr)
                .setJavaMethod(new StringBuffer().append(declaringTypeName).append(".").append(interfaceName).toString());

        if (sysLog.enableOperate() && sysLog.logArgs()) {
            String requestBody = null;
            if (ObjectUtil.isEmpty(args)) {
                String toJsonStr = JSONUtil.toJsonStr(pointArgs, jsonConfig);
                args = toJsonStr;
            } else {
                //requestBody = ServletUtils.getRequestContent(request);
                requestBody = ServletUtils.getRequestBody(request.getInputStream());
                JSON requestBodyJson = null;
                if (ObjectUtil.isEmpty(requestBody)) {
                    try {
                        requestBodyJson = JSONUtil.parse(args, jsonConfig);
                        requestBody = requestBodyJson.toString();
                    } catch (Exception e) {
                        //解析失败
                        log.error("[Error]:RequestBodyJson Parsing failed:{}", args);
                        log.error(e.getMessage());
                        requestBody = args;
                        operateLog.setJavaMethodArgsBodyToJson(false);
                    }
                }
                log.debug("argsJson:{}", requestBodyJson);
            }
            log.debug("args:{}", args);
            //记录请求参数
            String params = JSONUtil.parse(paramMap, jsonConfig).toString();
            if (getParamsMethod.contains(method)) {
                operateLog.setJavaMethodArgsParams(params);
            } else if (getBodyMethod.contains(method)) {
                operateLog.setJavaMethodArgsBody(requestBody);
            }

            AbstractOperateLogService service = SpringUtil.getBean(AbstractOperateLogService.class);
            Map<String, Object> beanToMap = BeanUtil.beanToMap(operateLog);
            service.createOperateLog(beanToMap);
        }
        return operateLog;
    }

    /**
     * 操作记录结束
     *
     * @param operateLog
     * @param bean
     * @return
     */
    public OperateLogInfo operateFinish(OperateLogInfo operateLog, Result bean) {
        Long startTime = operateLog.getStartTime();
        Long resultTime = bean.getResultTime();
        //获取毫秒数 东八区

        Long resultMilliSecond = resultTime;
        Long startMilliSecond = startTime;
        operateLog.setResultCode(bean.getCode());
        operateLog.setResultMsg(bean.getMessage());
        operateLog.setResultData(JSONUtil.toJsonStr(bean.getData(), jsonConfig));
        operateLog.setResultTime(resultTime);
        operateLog.setDuration(resultMilliSecond - startMilliSecond);
        AbstractOperateLogService service = SpringUtil.getBean(AbstractOperateLogService.class);
        Map<String, Object> beanToMap = BeanUtil.beanToMap(operateLog);
        service.updateOperateLog(beanToMap);
        return operateLog;
    }
}

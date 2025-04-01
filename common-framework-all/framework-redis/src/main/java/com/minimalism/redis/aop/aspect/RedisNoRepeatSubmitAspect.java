package com.minimalism.redis.aop.aspect;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.minimalism.abstractinterface.aop.AbstractRedisAspect;
import com.minimalism.redis.aop.redis.RedisNoRepeatSubmit;
import com.minimalism.redis.exception.RedisException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author yan
 * @Date 2024/5/24 0024 10:54
 * @Description
 */
@Aspect
@Slf4j
@Component
@Getter
public class RedisNoRepeatSubmitAspect implements AbstractRedisAspect {
    @Lazy
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 缓存参数缓存key模板
     */
    protected ThreadLocal<RedisCacheParameters> redisCacheThreadLocal = new ThreadLocal<>();

    public RedisCacheParameters getOne() {
        RedisCacheParameters one = redisCacheThreadLocal.get();
        if (one == null) {
            one = new RedisCacheParameters();
        }
        setOne(one);
        return one;
    }

    public RedisCacheParameters setOne(RedisCacheParameters one) {
        redisCacheThreadLocal.set(one);
        return one;
    }

    @Override
    @Pointcut("@annotation(com.minimalism.redis.aop.redis.RedisNoRepeatSubmit)")
    public void pointcutAspect() {
    }

    @Override
    @Around(value = "pointcutAspect()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        //joinPoint.proceed() 目标方法执行
        RedisNoRepeatSubmit annotation = getAnnotation(joinPoint, RedisNoRepeatSubmit.class);
        RedisCacheParameters one = getOne();
        try {
            if (annotation == null) {
                return joinPoint.proceed();
            }
            one = setRequesRedisCacheParameters(one, joinPoint);
        } finally {
            setOne(one);
        }
        String cacheName = annotation.cacheName();
        String key = annotation.key();
        TimeUnit timeUnit = annotation.timeUnit();
        long timout = annotation.timeout();
        String condition = annotation.condition();
        String value = annotation.value();
        String requestAsName = annotation.requestAsName();
        String addressAsName = annotation.addressAsName();
        String userIdAsName = annotation.userIdAsName();
        String requestPrefixHeaderAsName = annotation.requestPrefixHeaderAsName();
        String exceptionMessage = annotation.exceptionMessage();

        RedisCacheParameters parameters = one;
        Map<String, Object> request = parameters.getRequest();
        // 获取请求头
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String address = null, userId = null;
        if (attributes != null) {
            HttpServletRequest httpServletRequest = attributes.getRequest();
            address = httpServletRequest.getHeader(addressAsName);
            userId = httpServletRequest.getHeader(userIdAsName);
            if (StrUtil.isBlank(address)) {
                address = getClientIp(httpServletRequest);
            }
        }
        if (StrUtil.isBlank(requestPrefixHeaderAsName)) {
            requestPrefixHeaderAsName = StrUtil.EMPTY;
        }
        //不包含地址和当前userId的初始参数
        Map<String, Object> requestValue = BeanUtil.beanToMap(request);
        request.put(new StringBuffer(requestPrefixHeaderAsName).append(address).toString(), address);
        request.put(new StringBuffer(requestPrefixHeaderAsName).append(userIdAsName).toString(), userId);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(requestAsName, request);
        JSONObject jsonObject = new JSONObject(map);

        // 判断是否需要
        try {
            condition = effectiveSplicingString(condition, jsonObject, comparisonOperators, OperationType.condition);
        } catch (Exception e) {
            log.error("condition is error,condition:{}", condition);
        }

        boolean okCondition = verifiedOkCondition(condition);
        // 判断条件 判断是否需要
        if (okCondition) {
            cacheName = effectiveSplicingString(cacheName, jsonObject, CollUtil.newArrayList(splicer), OperationType.str);
            key = effectiveSplicingString(key, jsonObject, CollUtil.newArrayList(splicer), OperationType.str);
            String formatKey = String.format(templateKey, cacheName, key);

            Object o = redisTemplate.opsForValue().get(formatKey);
            Object result = StrUtil.isBlank(value) ? requestValue : value;
            Map<String, Object> toMap = ObjectUtil.isEmpty(o) ? new HashMap<>() : BeanUtil.beanToMap(o);
            if (ObjectUtil.isEmpty(o) ||
                    (ObjectUtil.isNotEmpty(o) && (!ObjectUtil.equals(toMap, requestValue)))) {
                redisTemplate.opsForValue().set(formatKey, result, timout, timeUnit);
            } else {
                throw new RedisException(exceptionMessage);
            }
        }

        //todo 缓存处理加锁
        return AbstractRedisAspect.super.around(joinPoint);

    }
}

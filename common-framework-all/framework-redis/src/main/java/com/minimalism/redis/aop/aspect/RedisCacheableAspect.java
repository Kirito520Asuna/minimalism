package com.minimalism.redis.aop.aspect;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.minimalism.abstractinterface.aop.AbstractRedisAspect;
import com.minimalism.redis.aop.redis.RedisCacheable;
import com.minimalism.redis.exception.RedisException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author yan
 * @Date 2024/7/26 0026 15:49:44
 * @Description
 */
@Aspect
@Slf4j
@Component
@Getter
public class RedisCacheableAspect implements AbstractRedisAspect {
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
    @Pointcut("@annotation(com.minimalism.redis.aop.redis.RedisCacheable)")
    public void pointcutAspect() {
    }

    @Override
    @Around(value = "pointcutAspect()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        RedisCacheable cacheable = getAnnotation(joinPoint, RedisCacheable.class);
        if (cacheable == null) {
            return AbstractRedisAspect.super.around(joinPoint);
        }
        setOne(setRequesRedisCacheParameters(getOne(), joinPoint));

        String cacheName = cacheable.cacheName();
        String key = cacheable.key();
        String condition = cacheable.condition();
        String requestAsName = cacheable.requestAsName();
        Class aClass = cacheable.classType();

        RedisCacheParameters one = getOne();
        Map<String, Object> request = one.getRequest();

        Map<String, Object> map = new LinkedHashMap<>();
        map.put(requestAsName, request);
        JSONObject jsonObject = new JSONObject(map);

        key = effectiveSplicingString(key, jsonObject, CollUtil.newArrayList(splicer), OperationType.str);

        String formatKey = String.format(templateKey, cacheName, key);

        // 判断是否需要缓存
        try {
            condition = effectiveSplicingString(condition, jsonObject, comparisonOperators, OperationType.condition);
        } catch (Exception e) {
            log.error("condition is error,condition:{}", condition);
            condition = cacheable.condition();
        }

        boolean okCondition = verifiedOkCondition(condition);
        // 判断条件 判断是否需要缓存
        Object o = null;
        if (okCondition) {
            if (cacheable.isHash()) {
                o = redisTemplate.opsForHash().get(cacheName, key);
            } else {
                o = redisTemplate.opsForValue().get(formatKey);
            }
            if (o != null) {
                Object bean = JSONUtil.toBean(JSONUtil.toJsonStr(o), aClass);
                o = bean;
                log.info("缓存命中，key:{},value:{}", formatKey, o);
            } else {
                Object around = AbstractRedisAspect.super.around(joinPoint);
                if (around != null) {
                    o = around;
                } else if (cacheable.throwException()) {
                    throw new RedisException(cacheable.exceptionMessage());
                }
            }
        }
        return o;
    }

}

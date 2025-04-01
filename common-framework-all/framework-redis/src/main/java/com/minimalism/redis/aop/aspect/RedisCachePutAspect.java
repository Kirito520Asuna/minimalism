package com.minimalism.redis.aop.aspect;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.minimalism.abstractinterface.aop.AbstractRedisAspect;
import com.minimalism.redis.aop.redis.RedisCachePut;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
public class RedisCachePutAspect implements AbstractRedisAspect {
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
    @Pointcut("@annotation(com.minimalism.redis.aop.redis.RedisCachePut)")
    public void pointcutAspect() {
    }

    @Override
    @SneakyThrows
    @Before(value = "pointcutAspect()")
    public void doBefore(JoinPoint joinPoint) {
        RedisCachePut cachePut = getAnnotation(joinPoint, RedisCachePut.class);
        if (cachePut == null) {
            return;
        }
        setOne(setRequesRedisCacheParameters(getOne(), joinPoint));
    }


    @SneakyThrows
    @Override
    @AfterReturning(pointcut = "pointcutAspect()", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        try {
            RedisCachePut cachePut = getAnnotation(joinPoint, RedisCachePut.class);
            if (cachePut == null) {
                return;
            }
            String cacheName = cachePut.cacheName();
            String key = cachePut.key();
            TimeUnit timeUnit = cachePut.timeUnit();
            long timout = cachePut.timeout();
            String condition = cachePut.condition();
            String value = cachePut.value();
            String requestAsName = cachePut.requestAsName();
            String responseAsName = cachePut.responseAsName();
            boolean openReplace = cachePut.openReplace();
            String[] replaceSplicingList = cachePut.replaceSplicingList();
            boolean random = cachePut.random();
            String randomRange = cachePut.randomRange();

            setOne(getOne().setResponse(JSONUtil.toBean(JSONUtil.toJsonStr(result),Map.class)));
            RedisCacheParameters one = getOne();
            Map<String, Object> request = one.getRequest();
            Map<String, Object> response = one.getResponse();

            Map<String, Object> map = new LinkedHashMap<>();
            map.put(requestAsName, request);
            map.put(responseAsName, response);
            JSONObject jsonObject = new JSONObject(map);

            cacheName = effectiveSplicingString(cacheName, jsonObject, CollUtil.newArrayList(splicer), OperationType.str);
            key = effectiveSplicingString(key, jsonObject, CollUtil.newArrayList(splicer), OperationType.str);
            if (StrUtil.isNotBlank(value)) {
                value = effectiveSplicingString(value, jsonObject, CollUtil.newArrayList(splicer), OperationType.str);
            }

            String formatKey = String.format(templateKey, cacheName, key);
            if (openReplace) {
                if (replaceSplicingList.length != 2) {
                    replaceSplicingList = new String[]{":", ":"};
                }
                formatKey = replaceKey(formatKey, replaceSplicingList);
            }
            // 判断是否需要缓存
            try {
                condition = effectiveSplicingString(condition, jsonObject, comparisonOperators, OperationType.condition);
            } catch (Exception e) {
                log.error("condition is error,condition:{}", condition);
                condition = cachePut.condition();
            }

            boolean okCondition = verifiedOkCondition(condition);
            // 判断条件 判断是否需要缓存
            if (okCondition) {
                Object setValue = result;
                if (StrUtil.isNotBlank(value)) {
                    setValue = value;
                }
                if (random) {
                    try {
                        String[] split = randomRange.split("~");
                        timout = RandomUtil.randomLong(Long.parseLong(split[0]), Long.parseLong(split[1]));
                    } catch (Exception e) {
                        log.error("randomRange is error,randomRange:{}", randomRange);
                        throw e;
                    }
                    redisTemplate.opsForValue().set(formatKey, setValue, timout, timeUnit);
                } else if (cachePut.isHash()) {
                    redisTemplate.opsForHash().put(cacheName, key, setValue);
                    if (timout > 0) {
                        //redisTemplate.opsForHash().getOperations()
                        //                .expire(cacheName, timout, timeUnit);
                        redisTemplate.expire(cacheName, timout, timeUnit);
                    }
                } else if (timout < 1) {
                    redisTemplate.opsForValue().set(formatKey, setValue);
                } else {
                    redisTemplate.opsForValue().set(formatKey, setValue, timout, timeUnit);
                }
                log.info("redis cache put key:{},value:{},timout:{}", formatKey, setValue, timout);
            }
        } finally {
            setOne(null);
        }

    }

}

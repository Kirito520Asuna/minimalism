package com.minimalism.redis.service;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

public interface RedisService {
    default Logger log() {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        return logger;
    }

    default RedisTemplate getRedisTemplate() {
        return SpringUtil.getBean(RedisTemplate.class);
    }

    /**
     * 获取缓存数据
     *
     * @param key key
     * @return
     */
    default Object get(String key) {
        return get("", false, key);
    }

    /**
     * 获取缓存数据
     *
     * @param cacheName 缓存名称
     * @param isHash    是否hash
     * @param key       key
     * @return
     */
    default Object get(String cacheName, boolean isHash, String key) {
        RedisTemplate redisTemplate = getRedisTemplate();
        Object value;
        if (isHash) {
            value = redisTemplate.opsForHash().get(cacheName, key);
        } else {
            value = redisTemplate.opsForValue().get(key);
        }

        if (value != null) {
            log().debug("缓存命中，key:{},value:{}", key, value);
        } else {
            log().error("缓存未命中，key:{}", key);
        }
        return value;
    }

    /**
     * 缓存数据
     *
     * @param key
     * @param value
     * @param timout
     * @param timeUnit
     * @return
     */
    default boolean save(String key, Object value, long timout, TimeUnit timeUnit) {
        return save(false, null, key, false, key, value, timout, timeUnit);
    }

    /**
     * 缓存数据
     *
     * @param random      是否随机
     * @param randomRange 随机范围 0~1
     * @param cacheName   缓存名称
     * @param isHash      是否hash
     * @param key         key
     * @param value       value
     * @param timeout     过期时间(>0时生效)
     * @param timeUnit    时间单位
     * @return
     */
    default boolean save(boolean random, String randomRange, String cacheName, boolean isHash, String key, Object value, long timeout, TimeUnit timeUnit) {
        RedisTemplate redisTemplate = getRedisTemplate();
        if (random) {
            try {
                String[] split = randomRange.split("~");
                timeout = RandomUtil.randomLong(Long.parseLong(split[0]), Long.parseLong(split[1]));
            } catch (Exception e) {
                log().error("随机数生成失败:{}", e);
                throw e;
            }
            redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
        } else if (isHash) {
            redisTemplate.opsForHash().put(cacheName, key, value);
            if (timeout > 0) {
                redisTemplate.expire(cacheName, timeout, timeUnit);
            }
        } else if (timeout < 1) {
            redisTemplate.opsForValue().set(key, value);
        } else {
            redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
        }
        log().debug("redis缓存数据成功，key:{},value:{},timeout:{}", key, value, timeout);
        return true;
    }

    /**
     * 删除数据
     * @param key
     * @return
     */
    default boolean del(String key) {
        return del(null,false, key);
    }

    /**
     * 删除数据
     *
     * @param cacheName 缓存名称
     * @param isHash    是否hash
     * @param key       key
     * @return
     */
    default boolean del(String cacheName, boolean isHash, String key) {
        RedisTemplate redisTemplate = getRedisTemplate();
        if (isHash) {
            redisTemplate.opsForHash().delete(cacheName, key);
            log().debug("删除hash缓存成功，key:{}", key);
        } else {
            redisTemplate.delete(key);
            log().debug("删除缓存成功，key:{}", key);
        }
        return true;
    }

}

package com.minimalism.redis.ip;

/**
 * @Author yan
 * @DateTime 2024/6/19 0:58:00
 * @Description
 */

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
@Slf4j @Component
public class RequestCounter {
    @Resource
    private RedissonClient redissonClient;
    private RMapCache<String, Integer> requestCounters;
    @PostConstruct
    public void init() {
        this.requestCounters = redissonClient.getMapCache("request_counters", StringCodec.INSTANCE);
    }



    /**
     * 记录请求次数
     *
     * @param ipAddress    请求者的IP地址
     * @param ttlIn 计数器的有效时间
     * @param timeUnit 计数器的有效时间 单位
     */
    public void recordRequest(String ipAddress, int ttlIn, TimeUnit timeUnit) {
        // 使用MapCache的getAndAdd方法原子性地增加计数并返回旧值
        // 同时设置计数器过期时间
        requestCounters.putIfAbsent(ipAddress, 1, ttlIn, timeUnit != null ? timeUnit : TimeUnit.SECONDS);
        requestCounters.addAndGet(ipAddress, 1);
    }

    /**
     * 获取某个IP地址的请求次数
     *
     * @param ipAddress IP地址
     * @return 请求次数，如果计数器已过期则返回null
     */
    public Integer getRequestCount(String ipAddress) {
        return requestCounters.get(ipAddress);
    }
    /**
     * 移除指定IP地址的请求计数器
     *
     * @param ipAddress IP地址
     */
    public void removeRequestCounter(String ipAddress) {
        requestCounters.remove(ipAddress);
    }

}
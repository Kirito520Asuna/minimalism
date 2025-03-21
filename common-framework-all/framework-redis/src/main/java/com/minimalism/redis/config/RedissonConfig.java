package com.minimalism.redis.config;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import com.minimalism.abstractinterface.config.AbstractRedissonConfig;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;


/**
 * @Author yan
 * @Date 2024/5/23 0023 10:02
 * @Description
 */
@Configuration
@EnableAspectJAutoProxy
@EnableCaching // 开启Spring Redis Cache，使用注解驱动缓存机制
@ConditionalOnBean(RedisConfiguration.class)
public class RedissonConfig implements AbstractRedissonConfig {

    @Resource
    @Lazy
    private RedisConfiguration configuration;
    @Resource
    @Lazy
    private Environment env;



    @Bean
    public RedissonClient redissonClient() {
        return initRedissonClient();
    }

    public RedissonClient initRedissonClient() {
        return AbstractRedissonConfig.super.getRedissonClient(configuration);
    }

    @Bean @Primary
    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        return initRedisTemplate(connectionFactory);
    }

    //    @Bean(name = "cacheDataManager")使自动装配失效 弃用
    public CacheManager cacheDataManager(RedisConnectionFactory connectionFactory) {
        log.info("初始化缓存管理器");
        List<String> list = Arrays.asList("");
        CacheManager cacheManager = initEntryTtlCacheManager(list, Duration.ofMinutes(5), connectionFactory);
        return cacheManager;
    }

    //    @Bean(name = "loginCacheManager")使自动装配失效 弃用
    public CacheManager loginCacheManager(RedisConnectionFactory connectionFactory) {
        String property = env.getProperty("spring.cache.cache-names");
        List<String> list = Arrays.asList(property);
        CacheManager cacheManager = initEntryTtlCacheManager(list, Duration.ofMinutes(5), connectionFactory);
        return cacheManager;
    }
}

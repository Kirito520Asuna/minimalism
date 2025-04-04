package com.minimalism.abstractinterface.config;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONNull;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.minimalism.redis.config.RedisConfiguration;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;


import java.io.IOException;
import java.text.DateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * @Author yan
 * @Date 2024/5/22 0022 9:50
 * @Description
 */
public interface AbstractRedissonConfig {
    Logger log = LoggerFactory.getLogger(AbstractRedissonConfig.class);
    String DEFAULT_REDIS = "127.0.0.1:6379";
    enum RedisMode {
        single, cluster,sentinel;
    }

    /**
     * 初始化RedissonClient
     *
     * @return
     */
    default RedissonClient initRedissonClient(RedisConfiguration configuration) {
        log.info("[init] RedissonClient");
        List<String> addresses = Arrays.asList(DEFAULT_REDIS);
        String redisPassword = null;
        Integer redisTimeout = null;
        String redisMode = "single";
        try {
            addresses = configuration.getAddresses();
            redisPassword = configuration.getRedisPassword();
            redisTimeout = configuration.getRedisTimeout();
            redisMode = configuration.getRedisMode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getRedissonClient(addresses, redisPassword, redisTimeout, redisMode);
    }
    default RedissonClient getRedissonClient(RedisConfiguration configuration){
        log.info("[init] [RedisProperties]  [RedissonClient]");
        Config config = new Config();
        BaseConfig baseConfig;
        RedisConfiguration.RedisMode redisModeEnum = configuration.getRedisModeEnum();
        RedisProperties redisProperties ;
        try {
            redisProperties = configuration.getRedisProperties();
        }catch (NullPointerException e){
            log.error("[error] RedisProperties  ... ");
            redisProperties = SpringUtil.getBean(RedisProperties.class);
        }
        log.info("RedisProperties:{}", JSONUtil.toJsonStr(redisProperties));
        switch(redisModeEnum){
            case cluster:
                RedisProperties.Cluster cluster = redisProperties.getCluster();
                List<String> nodes = cluster.getNodes();
                nodes = CollUtil.isEmpty(nodes)?CollUtil.newArrayList(DEFAULT_REDIS):nodes;
                // 集群模式 需要配置所有节点地址
                ClusterServersConfig clusterServersConfig = config.useClusterServers();
                clusterServersConfig.addNodeAddress(nodes.toArray(new String[0]));
                baseConfig = clusterServersConfig;
                break;
            case sentinel:
                RedisProperties.Sentinel sentinel = redisProperties.getSentinel();
                List<String> sentinelNodes = sentinel.getNodes();
                sentinelNodes = CollUtil.isEmpty(sentinelNodes)?CollUtil.newArrayList(DEFAULT_REDIS):sentinelNodes;
                // 哨兵模式
                SentinelServersConfig sentinelServersConfig = config.useSentinelServers();
                sentinelServersConfig
                        .setMasterName(sentinel.getMaster())
                        .addSentinelAddress(sentinelNodes.toArray(new String[0]));
                sentinelServersConfig.setPassword(sentinel.getPassword());
                sentinelServersConfig.setUsername(sentinel.getUsername());
                baseConfig = sentinelServersConfig;
                break;
            case single: // 单机模式 传递默认default
            default:
                List<String> addresses = CollUtil.newArrayList(
                        new StringBuffer("redis://").append(redisProperties.getHost())
                                .append(":")
                                .append(redisProperties.getPort()).toString()
                );

                SingleServerConfig singleServerConfig = config.useSingleServer();
                singleServerConfig.setAddress(addresses.stream().findFirst().orElse(new StringBuffer("redis://").append(DEFAULT_REDIS).toString()));
                singleServerConfig.setDatabase(redisProperties.getDatabase());
                baseConfig = singleServerConfig;
                break;
        }
        baseConfig.setUsername(redisProperties.getUsername());
        baseConfig.setPassword(redisProperties.getPassword());

        return Redisson.create(config);
    }
    default RedissonClient getRedissonClient(List<String> addresses, String redisPassword, Integer redisTimeout, String redisMode) {
        Config config = new Config();
        BaseConfig baseConfig;
        String defaultAddr = "redis://127.0.0.1:6379";
        switch (RedisMode.valueOf(redisMode)) {
            case cluster:
                // 集群模式 需要配置所有节点地址
                ClusterServersConfig clusterServersConfig = config.useClusterServers();
                clusterServersConfig.addNodeAddress(addresses.toArray(new String[0]));
                baseConfig = clusterServersConfig;
                break;
            case sentinel:
                // 哨兵模式
                SentinelServersConfig sentinelServersConfig = config.useSentinelServers();
                sentinelServersConfig
                        .setMasterName("master")
                        .addSentinelAddress(addresses.toArray(new String[0]));
                baseConfig = sentinelServersConfig;
                break;
            case single: // 单机模式 传递默认default
            default:
                SingleServerConfig singleServerConfig = config.useSingleServer();
                singleServerConfig.setAddress(addresses.stream().findFirst().orElse(defaultAddr));
                baseConfig = singleServerConfig;
                break;
        }

        if (redisTimeout != null) {
            baseConfig.setTimeout(redisTimeout);
        }
        if (StringUtils.hasText(redisPassword)) {
            baseConfig.setPassword(redisPassword);
        }

        // 如果需要支持 SSL/TLS
        // baseConfig.setSslEnableEndpointIdentification(false)
        //                   .setSslProvider(JdkSslProvider.builder().build());
        // or
        // singleServerConfig.setSslEnableEndpointIdentification(false)
        //                   .setSslProvider(JdkSslProvider.builder().build());
        if (false) {
            // 如果需要支持哨兵模式
            SentinelServersConfig sentinelServersConfig = config.useSentinelServers();
            sentinelServersConfig
                    .setMasterName("mymaster")
                    .addSentinelAddress("redis://127.0.0.1:26389", "redis://127.0.0.1:26379");
        }

        return Redisson.create(config);
    }

    /**
     * 初始化RedisTemplate
     *
     * @param connectionFactory
     * @return
     */
    default RedisTemplate<String, Object> initRedisTemplate(RedisConnectionFactory connectionFactory) {
        log.info("init RedisTemplate");
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        // 使用Jackson进行JSON序列化
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(initObjectMapper(null));
        
        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(serializer);

        // Hash的key也采用StringRedisSerializer的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 初始化ObjectMapper
     * @return
     */
    default ObjectMapper initObjectMapper(ObjectMapper objectMapper) {
        objectMapper = ObjectUtil.isEmpty(objectMapper)? new ObjectMapper():objectMapper;

        // 设置可见性
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // ❌ **删除这行，避免 @class 信息**
        // 序列化后添加类信息(不配置,序列化后就是一个Json字符串)
        // objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        // ✅ **去掉 `@class` 信息**
        objectMapper.deactivateDefaultTyping();
        // 将日期序列化为可读字符串而不是时间戳
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 设置时间模块(格式化，不设置，则输出默认格式)
        JavaTimeModule timeModule = new JavaTimeModule();
        // LocalDateTime
        String zoneId = "Asia/Shanghai";
        timeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN).withZone(ZoneId.of(zoneId))));
        timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN).withZone(ZoneId.of(zoneId))));
        // LocalDate
        timeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN).withZone(ZoneId.of(zoneId))));
        timeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN).withZone(ZoneId.of(zoneId))));
        // Date
        timeModule.addSerializer(Date.class, new DateSerializer(false, DateFormat.getDateInstance()));
        timeModule.addDeserializer(Date.class, new DateDeserializers.DateDeserializer());
        // 设置自定义时间模块
        objectMapper.registerModule(timeModule);

        SimpleModule hutoolModule = new SimpleModule();
        class HutoolJsonNullSerializer extends JsonSerializer<JSONNull> {
            @Override
            public void serialize(JSONNull value, JsonGenerator gen, SerializerProvider provider) throws IOException {
                gen.writeNull();  // 将 JSONNull 转换为 null
            }
        }
        hutoolModule.addSerializer(JSONNull.class, new HutoolJsonNullSerializer());
        objectMapper.registerModule(hutoolModule);

        // 7. 禁用空对象序列化失败（避免其他类似问题）
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        return objectMapper;
    }

    /*注解配置==start==*/
//    @Override
    @Nullable
    default CacheManager cacheManager() {
        return null;
    }

//    @Override
    @Nullable
    default CacheResolver cacheResolver() {
        return null;
    }

//    @Override
    @Nullable
    default KeyGenerator keyGenerator() {
        return null;
    }

//    @Override
    @Nullable
    default CacheErrorHandler errorHandler() {
        return null;
    }
    /*注解配置==end==*/

    /**
     *
     * 初始化缓存配置
     * @return
     */
    default RedisCacheConfiguration initRedisCacheConfiguration() {
        log.info("init RedisCacheConfiguration");
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        // 设置使用 : 单冒号，而不是双 :: 冒号，避免 Redis Desktop Manager 多余空格
        // 详细可见 https://blog.csdn.net/chuixue24/article/details/103928965 博客
        cacheConfiguration.computePrefixWith(cacheName -> cacheName + ":")
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(initObjectMapper(null))));
        return cacheConfiguration;
    }

    /**
     * 初始化缓存管理器
     * @param redisCacheNames
     * @param cacheConfiguration
     * @param redisConnectionFactory
     * @return
     */
    default CacheManager initCacheManager(List<String> redisCacheNames, RedisCacheConfiguration cacheConfiguration, RedisConnectionFactory redisConnectionFactory) {
        log.info("init CacheManager");
        RedisCacheManager redisCacheManager = RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfiguration)
                .transactionAware()
                .build();
        List<Cache> caches = Arrays.asList();
        List<String> cacheNames = new ArrayList<>(redisCacheManager.getCacheNames());
        cacheNames.stream().filter(o -> redisCacheNames.contains(o))
                .forEach(name -> {
                    Cache cache = redisCacheManager.getCache(name);
                    caches.add(cache);
                });

        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(caches); // 获取缓存对象

        return cacheManager;
    }

    /**
     * 初始化TTL缓存管理器
     * @param redisCacheNames
     * @param duration
     * @param redisConnectionFactory
     * @return
     */
    default CacheManager initEntryTtlCacheManager(List<String> redisCacheNames, Duration duration, RedisConnectionFactory redisConnectionFactory) {
        log.info("init EntryTtlCacheManager");
        RedisCacheConfiguration cacheConfiguration = initRedisCacheConfiguration();
        // 设置缓存过期时间
        if (duration != null) {
            cacheConfiguration.entryTtl(duration);
        }
        return initCacheManager(redisCacheNames, cacheConfiguration, redisConnectionFactory);
    }
}

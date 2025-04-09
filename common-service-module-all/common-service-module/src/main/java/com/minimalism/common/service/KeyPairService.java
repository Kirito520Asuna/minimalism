package com.minimalism.common.service;

import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.AbstractKeyPair;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.redis.aop.redis.RedisCacheEvict;
import com.minimalism.redis.aop.redis.RedisCachePut;
import com.minimalism.redis.aop.redis.RedisCacheable;
import com.minimalism.redis.aop.redis.RedisLock;
import com.minimalism.utils.key.KeyUtils;

import java.util.concurrent.TimeUnit;

public interface KeyPairService extends AbstractKeyPair, AbstractBean {
    @Override
    @RedisLock(key = "KEY&PAIR&LOCK:#rq.identity", requestAsName = "rq", exceptionMessage = "非法操作！")
    default String soleDecrypt(String identity, String content) throws Exception {
        return AbstractKeyPair.super.soleDecrypt(identity, content);
    }

    @Override
    @RedisCacheEvict(cacheName = "KEY&PAIR^INFO", key = "#rq.identity", requestAsName = "rq")
    default boolean delKey(String identity) {
        return false;
    }

    @Override
    @RedisCachePut(cacheName = "KEY&PAIR^INFO", key = "#rq.keyInfo.identity", value = "#rq.keyInfo",
            responseAsName = "re", requestAsName = "rq",
            timeout = 5, timeUnit = TimeUnit.MINUTES)
    default boolean saveKey(KeyUtils.KeyInfo keyInfo) {
        return false;
    }

    @Override
    @RedisCacheable(cacheName = "KEY&PAIR^INFO", key = "#rq.identity", requestAsName = "rq", classType = KeyUtils.KeyInfo.class)
    default KeyUtils.KeyInfo getKeyByIdentity(String identity) {
        return null;
    }

    /***
     * 一次一密
     * 每次请求前获取密钥
     *
     * S:服务端 G:生成 C:客户端 P:公钥 R:私钥
     *
     * S:=>G=>P,R  P as S-P ,R as S-R
     * S-P 传输 给 C
     * C: =>G=>P,R  P as C-P ,R as C-R
     * S-P 加密 C-P 传输 给 S
     *
     * S: S-R 解密 获取 C-P
     * G==>密钥
     * C-P 加密 密钥 传输 给 C
     * C: C-R 解密 获取 密钥
     * todo: 优化
     */

}

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
    @RedisCachePut(cacheName = "KEY&PAIR^INFO:#rq.keyInfo.identity", key = "#rq.keyInfo.publicKeyBase64", value = "#rq.keyInfo",
            responseAsName = "re", requestAsName = "rq", isHash = true,
            timeout = 5, timeUnit = TimeUnit.MINUTES)
    default boolean saveKey(KeyUtils.KeyInfo keyInfo) {
        return false;
    }

    @Override
    @RedisCacheable(cacheName = "KEY&PAIR^INFO", key = "#rq.keyInfo.identity", requestAsName = "rq", classType = KeyUtils.KeyInfo.class)
    default KeyUtils.KeyInfo getKeyByIdentity(String identity) {
        return null;
    }


}

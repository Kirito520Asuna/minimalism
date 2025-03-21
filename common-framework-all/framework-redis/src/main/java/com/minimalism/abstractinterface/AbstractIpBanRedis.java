package com.minimalism.abstractinterface;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.redis.ip.IPBan;
import com.minimalism.redis.ip.RequestCounter;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @Author yan
 * @DateTime 2024/6/19 0:33:50
 * @Description
 */
public interface AbstractIpBanRedis {


    Logger logger = Logger.getLogger(AbstractIpBanRedis.class.getName());

    //ip封禁
    default void redisIpBan(String ip, List<String> ipBanNotIps, IPBan ipBan, RequestCounter requestCounter) {
        Boolean ipBanEnable = ipBan.getIpBanEnable();
        if (ObjectUtil.isEmpty(ipBanEnable) || !ipBanEnable) {
            logger.info("ipBanEnable is false or null");
            return;
        }
        if (CollUtil.isEmpty(ipBanNotIps)) {
            ipBanNotIps = CollUtil.newArrayList();
        }
        List<String> ipBanNotIpList = ipBan.getIpBanNotIpList();
        ipBanNotIps.addAll(ipBanNotIpList);
        Long maxCount = ipBan.getMaxCount();
        String key = String.format(IPBan.IP_BAN_KEY_MODE, ip);
        RedisTemplate<String, Object> redisTemplate = SpringUtil.getBean(RedisTemplate.class);
        Boolean hasKey = redisTemplate.hasKey(key);

        if (ObjectUtil.equals(hasKey, true)) {
            //已经封禁
            return;
        }

        Integer requestCount = requestCounter.getRequestCount(ip);
        if (ObjectUtil.isNotEmpty(requestCount) && (maxCount.intValue() - requestCount.intValue()) < 1) {
            // 封禁
            Long ipBanTime = ipBan.getIpBanTime();
            TimeUnit ipBanTimeUnit = IPBan.getTimeUnitByName(ipBan.getIpBanTimeUnit());

            if (ObjectUtil.isNotEmpty(ipBanTime) && ipBanTime > 0) {
                redisTemplate.opsForValue().set(key, ip, ipBanTime, ipBanTimeUnit);
            } else {
                // 永久封禁
                redisTemplate.opsForValue().set(key, ip);
            }
        } else {
            Long ipTimeIn = ipBan.getIpTimeIn();
            TimeUnit ipTimeUnit = IPBan.getTimeUnitByName(ipBan.getIpTimeUnit());
            // 计数
            requestCounter.recordRequest(ip, ipTimeIn.intValue(), ipTimeUnit);
        }

    }

    //ip封禁移除
    default void redisIpBanRemove(String ip, RequestCounter requestCounter, RedisTemplate<String, Object> redisTemplate) {
        String key = String.format(IPBan.IP_BAN_KEY_MODE, ip);
        redisTemplate.delete(key);
        requestCounter.removeRequestCounter(ip);
    }
}

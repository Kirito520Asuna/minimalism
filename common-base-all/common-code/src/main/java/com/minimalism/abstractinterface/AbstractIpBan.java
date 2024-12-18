package com.minimalism.abstractinterface;

import cn.hutool.core.collection.CollUtil;

import java.util.List;
import java.util.logging.Logger;

/**
 * @Author yan
 * @DateTime 2024/6/19 0:02:30
 * @Description
 */
public interface AbstractIpBan {

    // ip白名单
    String ipBanNot = "127.0.0.1";

    //ip封禁
    default void ipBan(String ip, List<String> ipBanNotIps) {
        Logger logger = Logger.getLogger(this.getClass().getName());
        if (CollUtil.isEmpty(ipBanNotIps)) {
            ipBanNotIps = CollUtil.newArrayList();
        }
        if (!ipBanNotIps.contains(ipBanNot)) {
            ipBanNotIps.add(ipBanNot);
        }
        if (ipBanNotIps.contains(ip)) {
            logger.info("ip = " + ip + " 为 白名单 禁止封禁");
            return;
        }
        logger.info("ipBan ip = " + ip);
    }

    //ip封禁移除
    default void ipBanRemove(String ip) {
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.info("ipBanRemove ip = " +ip);
    }
}

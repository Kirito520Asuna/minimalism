package com.minimalism.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 配置类，用于管理IP封禁相关配置
 */
@Slf4j
//@Configuration
@Data
@AllArgsConstructor
//@Component
public class IPBanConfig {
    @Resource
    private Environment environment;

    // 是否开启
    @Value("${ip.ban.enable:true}")
    private String ipBanEnableStr;
    private Boolean ipBanEnable;

    public Boolean getIpBanEnable() {
        boolean ipBanEnable = false;
        String banEnableStr = getIpBanEnableStr();
        if (StrUtil.isNotBlank(banEnableStr)) {
            banEnableStr = banEnableStr.trim();
            ipBanEnable = Boolean.parseBoolean(banEnableStr)||ObjectUtil.equals(banEnableStr, "1");
        }
        return ipBanEnable;
    }

    // 允许最大次数
    @Value("${ip.maxCount:10}")
    private Long maxCount;
    // 多少时间内
    @Value("${ip.timeIn:10}")
    private Long ipTimeIn;
    // 时间单位
    @Value("${ip.timeUnit:SECONDS}")
    private String ipTimeUnit;
    // ban时间 -1 永久封禁
    @Value("${ip.ban.time:60}")
    private Long ipBanTime;
    // ban时间 单位
    @Value("${ip.ban.timeUnit:DAYS}")
    private String ipBanTimeUnit;
    // 禁止ban的ip
    @Value("${ip.ban.notIps:127.0.0.1}")
    private String ipBanNotIps;

    private static Map<String, TimeUnit> timeUnitMap = Maps.newLinkedHashMap();

    @PostConstruct
    public void init() {
        log.info("Initializing IPBanConfig...");
        initTimeUnitMap();
        log.info("IPBanConfig initialized.");
    }

    private void initTimeUnitMap() {
        log.info("Initializing timeUnitMap start");
        try {
            EnumSet<TimeUnit> allUnits = EnumSet.allOf(TimeUnit.class);
            allUnits.stream().forEach(timeUnit -> timeUnitMap.put(timeUnit.name(), timeUnit));
        } catch (Exception e) {
            log.error("Failed to initialize timeUnitMap", e);
            // 此处可以添加一些恢复或备用逻辑
        }
        log.info("timeUnitMap initialized OK.");
    }

    public static TimeUnit getTimeUnitByName(String name) {
        TimeUnit timeUnit = ObjectUtil.defaultIfNull(timeUnitMap.get(name), TimeUnit.SECONDS);
        if (!timeUnitMap.containsKey(name)) {
            log.warn("No such TimeUnit: {}", name);
            // 可以定义一个默认的TimeUnit，或者根据情况抛出异常
        }
        return timeUnit;
    }
}

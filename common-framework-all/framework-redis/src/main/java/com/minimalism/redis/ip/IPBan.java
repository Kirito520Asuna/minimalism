package com.minimalism.redis.ip;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 配置类，用于管理IP封禁相关配置
 */
@Slf4j
@Data
@AllArgsConstructor
public class IPBan {
   public static final String IP_BAN_KEY_MODE = "ipBan:ip:%s";
    // 是否开启
    private Boolean ipBanEnable;
    // 允许最大次数
    private Long maxCount;
    // 多少时间内
    private Long ipTimeIn;
    // 时间单位
    private String ipTimeUnit;

    // ban时间 -1 永久封禁
    private Long ipBanTime;
    // ban时间 单位
    private String ipBanTimeUnit;
    // 禁止ban的ip
    private String ipBanNotIps;

    private static Map<String, TimeUnit> timeUnitMap = new LinkedHashMap<>();

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
            e.printStackTrace();
            log.error("Failed to initialize timeUnitMap", e);
            // 此处可以添加一些恢复或备用逻辑
        }
        log.info("timeUnitMap initialized OK.");
    }

    public List<String> getIpBanNotIpList() {
        List<String> list = CollUtil.newArrayList();
        String banNotIps = getIpBanNotIps();
        if (StrUtil.isNotBlank(banNotIps)) {
            List<String> strings = Arrays.stream(banNotIps.replace(" ", "").replace("，", ",").split(",")).collect(Collectors.toList());
            list.addAll(strings);
        }
        return list;
    }

    public static TimeUnit getTimeUnitByName(String name) {
        if (timeUnitMap.containsKey(name)) {
            return timeUnitMap.get(name);
        } else {
            log.warn("No such TimeUnit: {}", name);
            // 可以定义一个默认的TimeUnit，或者根据情况抛出异常
            return TimeUnit.SECONDS; // 假定默认返回SECONDS
        }
    }
}

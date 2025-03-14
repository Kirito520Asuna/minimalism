package com.minimalism.kafka.service;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @Author yan
 * @Date 2025/3/14 23:39:05
 * @Description
 */
public interface KafkaService {
    default KafkaTemplate getTemplate() {
        return SpringUtil.getBean(KafkaTemplate.class);
    }

    void send(String topic, String json);
}

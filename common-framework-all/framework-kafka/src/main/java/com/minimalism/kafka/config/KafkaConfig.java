package com.minimalism.kafka.config;

import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.kafka.error.handler.GlobalKafkaListenerErrorHandler;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaResourceFactory;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;

import java.util.Map;

/**
 * @Author yan
 * @Date 2025/3/14 22:58:46
 * @Description
 */
@Configuration
@AllArgsConstructor
@ConditionalOnBean(KafkaProperties.class)
public class KafkaConfig {
    @Bean
    public KafkaListenerErrorHandler kafkaListenerErrorHandler() {
        return new GlobalKafkaListenerErrorHandler();
    }

}

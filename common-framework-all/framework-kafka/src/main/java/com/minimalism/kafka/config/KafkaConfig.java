package com.minimalism.kafka.config;

import com.minimalism.kafka.listener.handler.GlobalKafkaListenerErrorHandler;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;

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

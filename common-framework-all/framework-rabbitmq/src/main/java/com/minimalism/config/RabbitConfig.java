package com.minimalism.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimalism.abstractinterface.rabbitmq.AbstractRabbitMq;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yan
 * @Date 2023/7/31 0031 15:14
 * @Description
 */
@Configuration
public class RabbitConfig implements AbstractRabbitMq {
    @Bean
    @Override
    public MessageConverter JsonMessageConverter(ObjectMapper objectMapper) {
        return AbstractRabbitMq.super.JsonMessageConverter(objectMapper);
    }
}

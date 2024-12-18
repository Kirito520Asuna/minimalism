package com.minimalism.config;

import com.minimalism.abstractinterface.rabbitmq.AbstractRabbitMq;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yan
 * @Date 2024/7/26 0026 18:08:24
 * @Description 一般队列配置
 */
@Configuration
public class DirectRabbitConfig implements AbstractRabbitMq {
    //队列
    @Bean
    public Queue directQueue() {
        return AbstractRabbitMq.super.directQueue(DIRECT_QUEUE);
    }
    //Direct交换机
    @Bean
    public DirectExchange directExchange() {
        return AbstractRabbitMq.super.directExchange(DIRECT_EXCHANGE);
    }
    //绑定  将队列和交换机绑定
    @Bean
    public Binding bindingDirect() {
        return AbstractRabbitMq.super.bindingDirect(directQueue(), directExchange(), DIRECT_ROUTING);
    }
}

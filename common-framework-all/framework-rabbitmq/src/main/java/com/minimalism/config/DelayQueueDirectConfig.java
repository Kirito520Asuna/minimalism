package com.minimalism.config;


import com.minimalism.abstractinterface.rabbitmq.AbstractRabbitMq;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 延迟队列
 * 使用ttl和dlx实现
 * Direct模式
 *
 * @Author yan
 * @Date 2023/7/31 0031 14:59
 * @Description 死信队列配置
 */
@Configuration
public class DelayQueueDirectConfig implements AbstractRabbitMq {
    @Bean
    @Override
    public Queue buildTtlQueue() {
        return AbstractRabbitMq.super.buildTtlQueue();
    }
    @Bean
    @Override
    public DirectExchange buildTtlExchange() {
        return AbstractRabbitMq.super.buildTtlExchange();
    }
    @Bean
    @Override
    public DirectExchange buildDlxExchange() {
        return AbstractRabbitMq.super.buildDlxExchange();
    }
    @Bean
    @Override
    public Binding buildDlxBinding(Queue dlxQueue, DirectExchange dlxExchange) {
        return AbstractRabbitMq.super.buildDlxBinding(dlxQueue, dlxExchange);
    }
    @Bean
    @Override
    public Binding buildTtlBinding(Queue ttlQueue, DirectExchange ttlExchange) {
        return AbstractRabbitMq.super.buildTtlBinding(ttlQueue, ttlExchange);
    }
}

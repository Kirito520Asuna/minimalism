package com.minimalism.abstractinterface.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * @Author yan
 * @Date 2024/7/26 0026 17:42:42
 * @Description
 */
public interface AbstractRabbitMq {
    String TTL_QUEUE = "ttl.queue";
    String TTL_ROUTING_KEY = "ttl.queue.key";
    String TTL_EXCHANGE = "ttl.queue.exchange";
    String DLX_QUEUE = "dlx.queue";
    String DLX_ROUTING_KEY = "dlx.queue.key";
    String DLX_EXCHANGE = "dlx.queue.exchange";
    int QUEUE_EXPIRATION = 3000;
    String DIRECT_QUEUE = "DirectQueue";
    String DIRECT_EXCHANGE = "DirectExchange";
    String DIRECT_ROUTING = "DirectRouting";

    default Queue buildTtlQueue() {
        Queue build = QueueBuilder.durable(TTL_QUEUE).withArgument("x-dead-letter-exchange", DLX_EXCHANGE) // DLX
                .withArgument("x-dead-letter-routing-key", DLX_ROUTING_KEY) // dead letter携带的routing key
                .withArgument("x-message-ttl", QUEUE_EXPIRATION) // 设置队列的过期时间
                .build();
        return build;
    }

    default DirectExchange buildTtlExchange() {
        return new DirectExchange(TTL_EXCHANGE);
    }

    default DirectExchange buildDlxExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }

    default Binding buildDlxBinding(Queue dlxQueue, DirectExchange dlxExchange) {
        return BindingBuilder.bind(dlxQueue).to(dlxExchange).with(DLX_ROUTING_KEY);
    }

    default Binding buildTtlBinding(Queue ttlQueue, DirectExchange ttlExchange) {
        return BindingBuilder.bind(ttlQueue).to(ttlExchange).with(TTL_ROUTING_KEY);
    }
    //队列
    default Queue directQueue(String name) {
        return new Queue(name, true);
    }
    //Direct交换机
    default DirectExchange directExchange(String name) {
        return new DirectExchange(name);
    }
    //绑定  将队列和交换机绑定
    default Binding bindingDirect(Queue queue, DirectExchange exchange, String name) {
        return BindingBuilder.bind(queue).to(exchange).with(name);
    }

    /**
     * 解决方法:添加这个类进行序列化解析
     * 会自动识别
     * @param objectMapper json序列化实现类
     * @return mq 消息序列化工具
     */
    default MessageConverter JsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }


}

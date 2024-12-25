package com.minimalism.pojo;

import com.minimalism.abstractinterface.rabbitmq.AbstractRabbitMq;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;

/**
 * @Author yan
 * @Date 2024/12/26 上午6:50:59
 * @Description
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class QueueInfo implements AbstractRabbitMq {
    // 队列key
    String queueKey;
    // 交换机key
    String exchangeKey;
    // 路由key
    String routingKey;
    Queue queue;
    DirectExchange directExchange;
    Binding binding;

    public QueueInfo setQueueKey(String queueKey) {
        this.queueKey = queueKey;
        this.queue = buildQueue(queueKey);
        return this;
    }

    public QueueInfo setExchangeKey(String exchangeKey) {
        this.exchangeKey = exchangeKey;
        this.directExchange = buildExchange(exchangeKey);
        return this;
    }

    public QueueInfo setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
        this.binding = buildBinding(this.queue, this.directExchange, routingKey);
        return this;
    }

    public QueueInfo(String queueKey, String directExchangeKey, String routingKey) {
        this.queueKey = queueKey;
        this.exchangeKey = directExchangeKey;
        this.routingKey = routingKey;
        this.queue = buildQueue(queueKey);
        this.directExchange = buildExchange(directExchangeKey);
        this.binding = buildBinding(queue, directExchange, routingKey);
    }


}

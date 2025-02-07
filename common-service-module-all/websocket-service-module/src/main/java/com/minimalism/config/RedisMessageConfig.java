package com.minimalism.config;


import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.constant.WebSocket;
import com.minimalism.endpoint.WebSocketEndpoint;
import com.minimalism.message.listener.RedisMessageListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author yan
 * @Date 2025/2/5 13:02:50
 * @Description
 */
@Configuration
public class RedisMessageConfig {

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        Map<String, Class<? extends MessageListener>> listeners = new HashMap<>();
        listeners.put(WebSocket.WS_MSG + "*", RedisMessageListener.class);

        listeners.entrySet().forEach(entry -> {
            String pattern = entry.getKey();
            Class<? extends MessageListener> entryValue = entry.getValue();
            MessageListener messageListener = SpringUtil.getBean(entryValue);
            container.addMessageListener(messageListener, new PatternTopic(pattern));
        });

        return container;
    }

    private static void extracted(RedisMessageListenerContainer container) {
        // 订阅所有 ws:msg:* 频道
        MessageListener listener = new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                String channel = new String(message.getChannel());
                String msg = new String(message.getBody());
                SpringUtil.getBean(WebSocketEndpoint.class).handleRedisMessage(msg);
            }
        };


        RedisMessageListener listenerContainer = SpringUtil.getBean(RedisMessageListener.class);
        container.addMessageListener(listenerContainer, new PatternTopic(WebSocket.WS_MSG + "*"));
    }
}
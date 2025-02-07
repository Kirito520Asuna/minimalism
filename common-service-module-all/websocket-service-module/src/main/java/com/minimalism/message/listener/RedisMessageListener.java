package com.minimalism.message.listener;

import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.endpoint.WebSocketEndpoint;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * @Author yan
 * @Date 2025/2/5 13:12:46
 * @Description
 */
@Component
public class RedisMessageListener implements MessageListener {
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String msg = new String(message.getBody());
        SpringUtil.getBean(WebSocketEndpoint.class).handleRedisMessage(msg);
    }
}

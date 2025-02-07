package com.minimalism.service;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.endpoint.WebSocketEndpoint;
import com.minimalism.constant.WebSocket;
import com.minimalism.domain.Message;
import org.springframework.data.redis.core.RedisTemplate;

import javax.websocket.Session;

/**
 * @Author yan
 * @Date 2025/2/5 13:46:01
 * @Description
 */
public interface MessageService extends AbstractBean {

    default Session findSessionByUserId(String userId){
       return WebSocketEndpoint.findSessionByUserId(userId);
    }
    // --- 消息发送方法 ---

    /**
     * 发送本地消息
     * @param userId
     * @param msg
     */
    default void sendLocalMessage(String userId, Message msg) {
        info("sendLocalMessage-->msg:{}", msg);
        Session session = findSessionByUserId(userId);
        if (session != null && session.isOpen()) {
            String toJsonStr = JSONUtil.toJsonStr(msg);
            //String compressed = compress(message);
            session.getAsyncRemote().sendText(toJsonStr);
        }
    }

    /**
     * 发送Redis的跨实例消息
     * @param targetInstanceId
     * @param msg
     */
    default void sendCrossInstanceMessage(String targetInstanceId, Message msg) {
        info("sendCrossInstanceMessage-->targetInstanceId:{},msg:{}", targetInstanceId,msg);
        SpringUtil.getBean(RedisTemplate.class)
                .convertAndSend(WebSocket.WS_MSG + targetInstanceId, msg);
    }

    /**
     * 发送离线消息
     * @param message
     */
    default void sendOfflineMessage(Message message) {
        info("sendOfflineMessage-->message:{}", message);
        warn("离线消息暂未实现!");
    }

    // --- 消息处理方法 ---

    /**
     * 处理来自Redis的跨实例消息
     * @param messageJson
     */
    default void handleRedisMessage(String messageJson) {
        Message msg = JSONUtil.toBean(messageJson, Message.class);
        Session session = findSessionByUserId(msg.getTargetId());
        if (session != null) {
            info("handleRedisMessage");
            session.getAsyncRemote().sendText(messageJson);
        }
    }
}

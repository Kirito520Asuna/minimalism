package com.minimalism.service;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.domain.netty.NettyMessage;
import com.minimalism.endpoint.WebSocketEndpoint;
import com.minimalism.constant.websocket.WebSocket;
import com.minimalism.domain.Message;
import com.minimalism.enums.MessageType;
import com.minimalism.utils.object.ObjectUtils;
import org.springframework.data.redis.core.RedisTemplate;

import javax.websocket.Session;
import java.util.Map;

/**
 * @Author yan
 * @Date 2025/2/5 13:46:01
 * @Description
 */
public interface MessageService extends AbstractBean {

    default void onMessage(Message message) {
        String targetUserId = message.getTargetId();
        String targetUserKey = WebSocket.WS_USER + targetUserId;
        String instanceId = message.getSendInstanceId();
        // 查询目标用户所在实例
        String targetInstanceId = (String) SpringUtil.getBean(RedisTemplate.class)
                .opsForValue().get(targetUserKey);

        if (targetInstanceId == null) {
            sendOfflineMessage(message);
            return;
        }

        message.setTargetInstanceId(targetInstanceId);

        if (targetInstanceId.equals(instanceId)) {
            sendLocalMessage(targetUserId, message); // 本地发送
        } else {
            sendCrossInstanceMessage(targetInstanceId, message); // 跨实例发送
        }
    }

    default Session findSessionByUserId(String userId) {
        return WebSocketEndpoint.findSessionByUserId(userId);
    }
    // --- 消息发送方法 ---
    default NettyMessage createNettyMessage(String content, String senderId, String sessionUserId) {
        NettyMessage nettyMessage = JSONUtil.isTypeJSON(content) ?
                JSONUtil.toBean(content, NettyMessage.class) : null;
        if (ObjectUtils.isNotEmpty(nettyMessage)) {
            nettyMessage.setTo(sessionUserId);
            nettyMessage.setFrom(senderId);
        }
        return nettyMessage;
    }
    /**
     * 消息处理
     *
     * @param msg
     * @param sessionId
     */
    default Object messageDealWith(Message msg, String sessionId) {
        Object re = msg;
        String content = msg.getContent();
        String senderId = msg.getSenderId();
        String sessionUserId = (String) SpringUtil.getBean(RedisTemplate.class).opsForHash()
                .get(WebSocket.WS_INSTANCE + msg.getTargetInstanceId(), sessionId);

        MessageType messageType = MessageType.valueOf(msg.getType());

        info("接收到[{}]", messageType.getDesc());
        switch (messageType) {
            case text:
                re = createNettyMessage(content, senderId, sessionUserId);
                break;
            case image:
                re = createNettyMessage(content, senderId, sessionUserId);
                break;
            case system:
                re = JSONUtil.toBean(content, Map.class);
                break;
            default:

                break;
        }

        return re;
    }



    /**
     * 发送本地消息
     *
     * @param userId
     * @param msg
     */
    default void sendLocalMessage(String userId, Message msg) {
        info("sendLocalMessage-->msg:{}", msg);
        Session session = findSessionByUserId(userId);
        if (session != null && session.isOpen()) {
            String sessionId = session.getId();
            Object o = messageDealWith(msg, sessionId);
            String toJsonStr = JSONUtil.toJsonStr(o);

            //String compressed = compress(message);
            session.getAsyncRemote().sendText(toJsonStr);
        }
    }

    /**
     * 发送Redis的跨实例消息
     *
     * @param targetInstanceId
     * @param msg
     */
    default void sendCrossInstanceMessage(String targetInstanceId, Message msg) {
        info("sendCrossInstanceMessage-->targetInstanceId:{},msg:{}", targetInstanceId, msg);
        SpringUtil.getBean(RedisTemplate.class)
                .convertAndSend(WebSocket.WS_MSG + targetInstanceId, msg);
    }

    /**
     * 发送离线消息
     *
     * @param message
     */
    default void sendOfflineMessage(Message message) {
        info("[离线消息]handleOfflineMessage-->message:{}", message);
        info("sendOfflineMessage-->message:{}", message);
        warn("离线消息暂未实现!");
    }

    // --- 消息处理方法 ---

    /**
     * 处理来自Redis的跨实例消息
     *
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

package com.minimalism.endpoint;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.domain.Message;
import com.minimalism.constant.websocket.WebSocket;
import com.minimalism.service.MessageService;
import com.minimalism.utils.object.ObjectUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/{userId}")
@Component
public class WebSocketEndpoint implements AbstractBean {


    // 本地存储当前实例的在线用户（线程安全）
    public static final Set<String> LOCAL_USER_IDS = ConcurrentHashMap.newKeySet();

    // 本地会话存储（线程安全）
    public static final ConcurrentHashMap<String, Session> SESSION_MAP = new ConcurrentHashMap<>();

    // 静态依赖注入
    @Resource
    private RedisTemplate<String, String> redisTemplate = getRedisTemplate();
    @Resource
    private NacosDiscoveryProperties nacosProperties = getNacosDiscoveryProperties();

    // 当前实例ID（格式：ip:port）


    // --- 生命周期管理 ---
    @PostConstruct
    public void init() {
        redisTemplate = getRedisTemplate();
    }

    @PreDestroy
    public void destroy() {
        // 清理Redis中当前实例的所有数据
        redisTemplate.delete(WebSocket.WS_INSTANCE + getInstanceId());
        LOCAL_USER_IDS.forEach(userId ->
                redisTemplate.delete(WebSocket.WS_USER + userId)
        );
    }

    public static String getInstanceId() {
        NacosDiscoveryProperties nacosDiscoveryProperties = getNacosDiscoveryProperties();
        return nacosDiscoveryProperties.getIp() + ":" + nacosDiscoveryProperties.getPort();
    }

    public static RedisTemplate getRedisTemplate() {
        return SpringUtil.getBean(RedisTemplate.class);
    }

    public static NacosDiscoveryProperties getNacosDiscoveryProperties() {
        return SpringUtil.getBean(NacosDiscoveryProperties.class);
    }

    // --- WebSocket事件处理 ---
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        String sessionId = session.getId();
        String instanceId = getInstanceId();
        String userKey = WebSocket.WS_USER + userId;
        String instanceKey = WebSocket.WS_INSTANCE + instanceId;

        info("WebSocket连接已建立，实例ID：{}，用户ID：{}", instanceId, userId);
        // 存储会话和用户信息
        SESSION_MAP.put(sessionId, session);
        LOCAL_USER_IDS.add(userId);

        // 更新Redis映射（用户->实例）
        redisTemplate.opsForValue().set(userKey, instanceId);
        // 存储实例的会话信息（实例->会话ID:用户ID）
        redisTemplate.opsForHash().put(instanceKey, sessionId, userId);
    }

    @OnClose
    public void onClose(Session session, @PathParam("userId") String userId) {
        String sessionId = session.getId();
        String instanceId = getInstanceId();
        String userKey = WebSocket.WS_USER + userId;
        String instanceKey = WebSocket.WS_INSTANCE + instanceId;

        info("WebSocket连接已关闭，实例ID：{}，用户ID：{}", instanceId, userId);
        // 清理本地存储
        SESSION_MAP.remove(sessionId);
        LOCAL_USER_IDS.remove(userId);

        // 清理Redis数据
        redisTemplate.opsForHash().delete(instanceKey, sessionId);
        redisTemplate.delete(userKey);
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("userId") String senderId) {
        info("收到消息: {}", message);
        try {
            Message msg = JSONUtil.toBean(message, Message.class);
            String targetUserId = msg.getTargetId();
            String targetUserKey = WebSocket.WS_USER + targetUserId;
            String instanceId = getInstanceId();
            // 查询目标用户所在实例
            String targetInstanceId = redisTemplate.opsForValue().get(targetUserKey);

            if (targetInstanceId == null) {
                handleOfflineMessage(msg);
                return;
            }

            msg.setTargetInstanceId(targetInstanceId);
            msg.setSendInstanceId(instanceId);

            if (targetInstanceId.equals(instanceId)) {
                sendLocalMessage(targetUserId, msg); // 本地发送
            } else {
                sendCrossInstanceMessage(targetInstanceId, msg); // 跨实例发送
            }
        } catch (Exception e) {
            // 异常处理逻辑
            error("处理消息时发生异常: {}", e.getMessage());
        }
    }

    // 获取当前实例在线用户列表
    public static Set<String> getLocalOnlineUsers() {
        return Collections.unmodifiableSet(LOCAL_USER_IDS);
    }

    // --- 辅助方法 ---
    public static Session findSessionByUserId(String userId) {
        String key = WebSocket.WS_INSTANCE + getInstanceId();

        RedisTemplate<String, String> redisTemplate = getRedisTemplate();
        return SESSION_MAP.values().stream()
                .filter(session -> {
                    String sessionUserId = (String) redisTemplate.opsForHash()
                            .get(key, session.getId());
                    return ObjectUtils.equals(userId, sessionUserId);
                })
                .findFirst()
                .orElse(null);
    }

    // --- 消息发送方法 ---
    // 发送本地消息
    public void sendLocalMessage(String userId, Message msg) {
        info("[本地消息]sendLocalMessage-->msg:{}", JSONUtil.toJsonStr(msg));
        Session session = findSessionByUserId(userId);
        if (session != null && session.isOpen()) {
            String toJsonStr = JSONUtil.toJsonStr(msg);
            //String compressed = compress(message);
            session.getAsyncRemote().sendText(toJsonStr);
        }
    }

    // 发送Redis的跨实例消息
    private void sendCrossInstanceMessage(String targetInstanceId, Message msg) {
        info("[跨实例消息]sendCrossInstanceMessage-->targetInstanceId:{},msg:{}", targetInstanceId, msg);
        redisTemplate.convertAndSend(WebSocket.WS_MSG + targetInstanceId, msg);
        //String url = NacosUtils.getUrl();
        //OkHttpUtils.post(url, msg);
    }

    // 处理来自Redis的跨实例消息
    public void handleRedisMessage(String messageJson) {
        Message msg = JSONUtil.toBean(messageJson, Message.class);
        Session session = findSessionByUserId(msg.getTargetId());
        if (session != null) {
            info("[处理跨实例消息]handleRedisMessage");
            session.getAsyncRemote().sendText(messageJson);
        }
    }

    private void handleOfflineMessage(Message msg) {
        info("[离线消息]handleOfflineMessage");
        // 离线消息存储逻辑（如存入数据库）
        SpringUtil.getBean(MessageService.class).sendOfflineMessage(msg);
    }
}
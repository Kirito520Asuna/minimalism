package com.minimalism.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.minimalism.domain.Message;
import com.minimalism.enums.MessageType;
import com.minimalism.exception.GlobalCustomException;
import com.minimalism.openfeign.factory.interfaces.ImClient;
import com.minimalism.pojo.openfeign.ChatMessage;
import com.minimalism.result.Result;
import com.minimalism.service.MessageService;
import com.minimalism.utils.object.ObjectUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @Author yan
 * @Date 2025/2/5 13:57:02
 * @Description
 */
@Service
public class MessageServiceImpl implements MessageService {
    @Override
    public void sendOfflineMessage(Message message) {
        //todo : 消息转发
        MessageService.super.sendOfflineMessage(message);
        String content = message.getContent();
        if(JSONUtil.isTypeJSON(content)){
            Map<String,Object> map = JSONUtil.toBean(content, Map.class);
        }

        if (ObjectUtils.equals(message.getType(), MessageType.system)) {

            String json = "{\n" +
                    "            from: sendUserId,\n" +
                    "            to: acceptUserId.value,\n" +
                    "            chatId: chatId2,\n" +
                    "            message: content,\n" +
                    "            type: 2(未定义类型重构可重新定义)\n" +
                    "          }";
        } else if (ObjectUtils.equals(message.getType(), MessageType.text)) {

        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatId(Long.parseLong("chatId"));
        chatMessage.setSendUserId(Long.parseLong("sendUserId"));
        chatMessage.setContent("message");
        chatMessage.setType(com.minimalism.enums.im.MessageType.valueOf("type"));
        chatMessage.setTime(LocalDateTime.now());
        Result result = SpringUtil.getBean(ImClient.class).sendMessage(chatMessage);
        if (!result.validateOk()) {
            throw new GlobalCustomException(result.getMessage());
        }
    }
}

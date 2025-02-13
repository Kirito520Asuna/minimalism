package com.minimalism.im.service.chat;



import com.baomidou.mybatisplus.extension.service.IService;
import com.minimalism.im.domain.chat.ChatMessage;

import java.util.List;

/**
 * @Author minimalism
 * @Date 2023/8/10 0010 14:22
 * @Description
 */
public interface ChatMessageService extends IService<ChatMessage> {


    /**
     * 聊天窗口聊天记录
     *
     * @param chatId
     * @param userId
     * @return
     */
    List<ChatMessage> getList(Long chatId, Long userId);

    /**
     * 发送消息
     * @param chatMessage
     */
    void sendMessage(ChatMessage chatMessage);
}

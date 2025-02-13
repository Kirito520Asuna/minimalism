package com.minimalism.im.service.chat;


import com.baomidou.mybatisplus.extension.service.IService;
import com.minimalism.im.domain.chat.ChatWindow;
import com.minimalism.enums.im.ChatType;

/**
 * @Author minimalism
 * @Date 2023/8/10 0010 14:22
 * @Description
 */
public interface ChatWindowService extends IService<ChatWindow> {


        ChatWindow getChatWindow(Long uid, Long tid, ChatType chatType);
    }

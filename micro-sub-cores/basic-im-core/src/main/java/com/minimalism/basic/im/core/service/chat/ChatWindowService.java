package com.minimalism.basic.im.core.service.chat;


import com.minimalism.basic.core.enums.im.ChatType;
import com.minimalism.im.domain.chat.ChatWindow;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Author minimalism
 * @Date 2023/8/10 0010 14:22
 * @Description
 */
public interface ChatWindowService extends IService<ChatWindow> {


        ChatWindow getChatWindow(Long uid, Long tid, ChatType chatType);
    }

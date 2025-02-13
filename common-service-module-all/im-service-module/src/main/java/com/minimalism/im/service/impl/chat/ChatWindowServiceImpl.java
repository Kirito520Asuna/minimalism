package com.minimalism.im.service.impl.chat;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minimalism.constant.DataSourceName;
import com.minimalism.im.domain.chat.ChatWindow;
import com.minimalism.enums.im.ChatType;
import com.minimalism.im.mapper.chat.ChatWindowMapper;
import com.minimalism.im.service.chat.ChatWindowService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author minimalism
 * @Date 2023/8/10 0010 14:22
 * @Description
 */
@Service @DS(DataSourceName.im)
public class ChatWindowServiceImpl extends ServiceImpl<ChatWindowMapper, ChatWindow> implements ChatWindowService {

    @Resource
    private ChatWindowMapper dao;

    @Override
    public ChatWindow getChatWindow(Long uid, Long tid, ChatType chatType) {
        return dao.getChatWindow(uid,tid,chatType);
    }
}

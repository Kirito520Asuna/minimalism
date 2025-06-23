package com.minimalism.basic.im.core.service.impl.chat;

import com.minimalism.basic.core.constant.datasource.DataSourceName;
import com.minimalism.basic.core.enums.im.ChatType;
import com.minimalism.im.domain.chat.ChatWindow;
import com.minimalism.im.mapper.chat.ChatWindowMapper;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minimalism.basic.im.core.service.chat.ChatWindowService;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;


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

package com.minimalism.im.mapper.chat;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minimalism.abstractinterface.mapper.MpMapper;
import com.minimalism.im.domain.chat.ChatWindow;
import com.minimalism.enums.im.ChatType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author yan
 * @Date 2023/8/10 0010 14:22
 * @Description
 */
@Mapper
public interface ChatWindowMapper extends MpMapper<ChatWindow> {
    ChatWindow getChatWindow(@Param("uid") Long uid, @Param("tid") Long tid, @Param("chatType") ChatType chatType);

}
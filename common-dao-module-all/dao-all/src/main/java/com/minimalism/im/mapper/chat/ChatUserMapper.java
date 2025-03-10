package com.minimalism.im.mapper.chat;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minimalism.abstractinterface.mapper.MpMapper;
import com.minimalism.enums.im.ChatType;
import com.minimalism.im.domain.chat.ChatUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author yan
 * @Date 2023/8/10 0010 14:38
 * @Description
 */
@Mapper
public interface ChatUserMapper extends MpMapper<ChatUser> {

    boolean exitChatId(@Param("userId2") Long userId2, @Param("userId") Long userId, @Param("oneOnOneChat") ChatType oneOnOneChat);
    Long getChatId(@Param("userId2") Long userId2, @Param("userId") Long userId, @Param("oneOnOneChat") ChatType oneOnOneChat);
}
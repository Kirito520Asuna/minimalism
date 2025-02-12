package com.minimalism.im.mapper.chat;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minimalism.im.domain.chat.ChatUser;
import com.minimalism.im.domain.enums.ChatType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author yan
 * @Date 2023/8/10 0010 14:38
 * @Description
 */
@Mapper
public interface ChatUserMapper extends BaseMapper<ChatUser> {

    boolean exitChatId(@Param("userId2") Long userId2, @Param("userId") Long userId, @Param("oneOnOneChat") ChatType oneOnOneChat);
    Long getChatId(@Param("userId2") Long userId2, @Param("userId") Long userId, @Param("oneOnOneChat") ChatType oneOnOneChat);
}
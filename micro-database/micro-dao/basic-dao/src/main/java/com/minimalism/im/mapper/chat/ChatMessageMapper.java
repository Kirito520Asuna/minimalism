package com.minimalism.im.mapper.chat;

import com.minimalism.im.domain.chat.ChatMessage;
import com.minimalism.mp.abs.mapper.MpMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author yan
 * @Date 2023/8/10 0010 14:22
 * @Description
 */
@Mapper
public interface ChatMessageMapper extends MpMapper<ChatMessage> {
    List<ChatMessage> getList(@Param("chatId") Long chatId);
}
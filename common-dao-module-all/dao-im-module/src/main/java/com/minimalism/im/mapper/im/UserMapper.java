package com.minimalism.im.mapper.im;



import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minimalism.enums.im.ChatType;
import com.minimalism.im.domain.im.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author yan
 * @Date 2023/8/7 0007 10:39
 * @Description
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    User getById(@Param("id")Long id);


    List<User> getFriends(User user);

    List<User> applyList(@Param("userId") Long userId);

    List<User> getUsers(User user);

    User getUser(@Param("chatId") Long chatId, @Param("userId") Long userId, @Param("chatType") ChatType chatType);

    User getOneUser(@Param("userId") Long userId, @Param("nowUserId") Long nowUserId);
}
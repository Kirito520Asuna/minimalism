package com.minimalism.im.service.im;


import com.baomidou.mybatisplus.extension.service.IService;
import com.minimalism.im.domain.im.Friend;
import com.minimalism.vo.user.UserVo;

import java.util.List;

/**
 * @Author minimalism
 * @Date 2023/8/7 0007 10:36
 * @Description
 */
public interface FriendService extends IService<Friend> {


    /**
     * @param userId
     * @param keyword
     * @return
     */
        List<UserVo> getFriends(Long userId, String keyword);
    }

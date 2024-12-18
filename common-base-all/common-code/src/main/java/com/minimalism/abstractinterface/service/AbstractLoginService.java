package com.minimalism.abstractinterface.service;

import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.exception.GlobalCustomException;
import com.minimalism.pojo.TokenInfo;
import com.minimalism.pojo.User;
import com.minimalism.pojo.UserInfo;
import com.minimalism.utils.object.ObjectUtils;

/**
 * @Author yan
 * @Date 2024/11/5 上午12:48:32
 * @Description
 */
public interface AbstractLoginService {
    /**
     * 注册账号
     *
     * @return
     */
    default User register(String nickname, String password, String password2) {
        return SpringUtil.getBean(AbstractUserService.class).register(nickname, password, password2);
    }

    /**
     * 登录
     *
     * @param user
     * @return
     */
    default TokenInfo login(UserInfo user){
        return SpringUtil.getBean(AbstractUserService.class).login(user);
    }

    /**
     * 从redis中获取用户信息(redis中存在数据表示在线状态)
     *
     * @param id
     * @return
     */
   default User getOneRedis(String id){
       return SpringUtil.getBean(AbstractUserService.class).getOneRedis(id);
   }

    /**
     * 账号退出(移除redis数据)
     *
     * @param id
     * @return
     */
    void logout(String id);

}

package com.minimalism.auth.shiro.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.auth.shiro.pojo.UserBase;
import com.minimalism.auth.shiro.service.AbsUserDetailsService;
import com.minimalism.auth.shiro.utils.AuthShiroContextUtil;
import com.minimalism.basic.core.abs.auth.service.AbstractLoginService;
import com.minimalism.basic.core.abs.auth.service.AbstractUserDetailsService;
import com.minimalism.basic.core.pojo.auth.TokenInfo;
import com.minimalism.basic.core.pojo.auth.UserInfo;


/**
 * @Author yan
 * @Date 2025/6/12 23:32:47
 * @Description
 */
public class SimpleLoginShiroService implements AbstractLoginService {

    /**
     * @param userInfo
     * @return
     */
    @Override
    public TokenInfo login(UserInfo userInfo) {
        TokenInfo tokenInfo = AbstractLoginService.super.login(userInfo);
        String username = userInfo.getUsername();

        UserBase user1 = SpringUtil.getBean(AbsUserDetailsService.class).loadUserByUsername(username);
        SpringUtil.getBean(AbsUserDetailsService.class).login(user1.getUser().getId(), username, user1.getPassword());
        return tokenInfo;
    }

    @Override
    public String getCurrentUserId() {
        return AuthShiroContextUtil.getUserIdNoThrow();
    }

    /**
     * @param id
     */
    @Override
    public void logout(String id) {
        SpringUtil.getBean(AbstractUserDetailsService.class)
                .logout(id);
    }

}

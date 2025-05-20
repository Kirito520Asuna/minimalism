package com.minimalism.shiro.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.shiro.abs.AbstractUserDetailsByShiroService;
import com.minimalism.abstractinterface.service.AbstractLoginService;
import com.minimalism.shiro.config.ShiroConfig;
import com.minimalism.pojo.TokenInfo;
import com.minimalism.pojo.UserInfo;
import com.minimalism.shiro.pojo.UserBase;
import com.minimalism.shiro.utils.SecurityContextUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

/**
 * @Author yan
 * @Date 2024/11/5 上午1:22:02
 * @Description
 */
@Service
//@ConditionalOnExpression("${authorization.shiro.enable:false} && !${authorization.security.enable:true}")
//@ConditionalOnExpression(ExpressionConstants.authorizationShiroAllExpression)
@ConditionalOnBean(ShiroConfig.class)
public class LoginServiceImpl implements AbstractLoginService {

    /**
     * @param userInfo
     * @return
     */
    @Override
    public TokenInfo login(UserInfo userInfo) {
        TokenInfo tokenInfo = AbstractLoginService.super.login(userInfo);
        String username = userInfo.getUsername();

        UserBase user1 = SpringUtil.getBean(AbstractUserDetailsByShiroService.class).loadUserByUsername(username);
        SpringUtil.getBean(AbstractUserDetailsByShiroService.class).login(user1.getUser().getId(), username, user1.getPassword());
        return tokenInfo;
    }

    @Override
    public String getCurrentUserId() {
        return SecurityContextUtil.getUserIdNoThrow();
    }

    /**
     * @param id
     */
    @Override
    public void logout(String id) {
        SpringUtil.getBean(AbstractUserDetailsByShiroService.class)
                .logout(id);
    }
}

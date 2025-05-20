package com.minimalism.security.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.service.AbstractLoginService;
import com.minimalism.abstractinterface.service.AbstractUserService;
import com.minimalism.security.config.SecurityConfig;
import com.minimalism.pojo.TokenInfo;
import com.minimalism.pojo.UserInfo;
import com.minimalism.security.utils.SecurityContextUtil;
import com.minimalism.security.service.AbsUserDetailsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2024/11/5 上午1:22:02
 * @Description
 */
@Service
//@ConditionalOnExpression("${authorization.security.enable:true} &&!${authorization.shiro.enable:false}")
//@ConditionalOnExpression(ExpressionConstants.authorizationSecurityAllExpression)
@ConditionalOnBean(SecurityConfig.class)
public class LoginServiceImpl implements AbstractLoginService {

    /**
     * @param userInfo
     * @return
     */
    @Override
    public TokenInfo login(UserInfo userInfo) {
        TokenInfo tokenInfo = AbstractLoginService.super.login(userInfo);

        UserInfo oneByUserName = SpringUtil.getBean(AbstractUserService.class).getOneByUserName(userInfo.getUsername());
        SecurityContextUtil.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(oneByUserName.getId(),null,
                oneByUserName.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())));
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
        SpringUtil.getBean(AbsUserDetailsService.class)
                .logout(id);
    }
}

package com.minimalism.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.service.AbstractLoginService;
import com.minimalism.abstractinterface.service.AbstractUserService;
import com.minimalism.config.security.SecurityConfig;
import com.minimalism.constant.ExpressionConstants;
import com.minimalism.pojo.TokenInfo;
import com.minimalism.pojo.UserInfo;
import com.minimalism.security.SecurityContextUtil;
import com.minimalism.service.AbstractUserDetailsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
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

    /**
     * @param id 
     */
    @Override
    public void logout(String id) {
        SpringUtil.getBean(AbstractUserDetailsService.class)
                .logout(id);
    }
}

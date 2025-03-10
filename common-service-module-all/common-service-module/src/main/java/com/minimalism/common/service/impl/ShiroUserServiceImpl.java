package com.minimalism.common.service.impl;

import com.minimalism.abstractinterface.service.config.AbstractAuthShiroConfig;
import com.minimalism.common.service.CommonUserService;
import com.minimalism.config.security.SecurityConfig;
import com.minimalism.utils.shiro.SecurityContextUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

/**
 * @Author yan
 * @Date 2025/3/7 0:41:29
 * @Description
 */
@Service
//@ConditionalOnExpression(ExpressionConstants.authorizationShiroAllExpression)
@ConditionalOnBean(AbstractAuthShiroConfig.class)
//@ConditionalOnMissingBean(AbstractAuthShiroConfig.class)
public class ShiroUserServiceImpl implements CommonUserService {
    @Override
    public String getUserId() {
        return SecurityContextUtil.getUserIdNoThrow();
    }
}

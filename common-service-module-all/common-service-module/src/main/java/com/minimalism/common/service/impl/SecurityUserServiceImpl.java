package com.minimalism.common.service.impl;

import com.minimalism.abstractinterface.service.config.AbstractAuthSecurityConfig;
import com.minimalism.abstractinterface.service.config.AbstractAuthShiroConfig;
import com.minimalism.common.service.CommonUserService;
import com.minimalism.config.security.SecurityConfig;
import com.minimalism.config.shiro.ShiroConfig;
import com.minimalism.constant.ExpressionConstants;
import com.minimalism.security.SecurityContextUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

/**
 * @Author yan
 * @Date 2025/3/7 0:42:22
 * @Description
 */
@Service
//@ConditionalOnExpression(ExpressionConstants.authorizationSecurityAllExpression)
@ConditionalOnBean(AbstractAuthSecurityConfig.class)
//@ConditionalOnMissingBean(ShiroConfig.class)
public class SecurityUserServiceImpl implements CommonUserService {
    @Override
    public String getUserId() {
        return SecurityContextUtil.getUserIdNoThrow();
    }
}

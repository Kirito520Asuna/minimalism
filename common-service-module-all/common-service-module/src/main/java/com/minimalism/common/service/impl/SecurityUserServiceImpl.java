package com.minimalism.common.service.impl;

import com.minimalism.common_code.abs.service.config.AbsAuthSecurityConfig;
import com.minimalism.common.service.CommonUserService;
import com.minimalism.security.utils.SecurityContextUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

/**
 * @Author yan
 * @Date 2025/3/7 0:42:22
 * @Description
 */
@Service
//@ConditionalOnExpression(ExpressionConstants.authorizationSecurityAllExpression)
@ConditionalOnBean(AbsAuthSecurityConfig.class)
//@ConditionalOnMissingBean(ShiroConfig.class)
public class SecurityUserServiceImpl implements CommonUserService {
    @Override
    public String getUserId() {
        return SecurityContextUtil.getUserIdNoThrow();
    }
}

package com.minimalism.common.service.impl;

import com.minimalism.abstractinterface.service.config.AbsAuthShiroConfig;
import com.minimalism.common.service.CommonUserService;
import com.minimalism.shiro.utils.SecurityContextUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

/**
 * @Author yan
 * @Date 2025/3/7 0:41:29
 * @Description
 */
@Service
//@ConditionalOnExpression(ExpressionConstants.authorizationShiroAllExpression)
@ConditionalOnBean(AbsAuthShiroConfig.class)
//@ConditionalOnMissingBean(AbstractAuthShiroConfig.class)
public class ShiroUserServiceImpl implements CommonUserService {
    @Override
    public String getUserId() {
        return SecurityContextUtil.getUserIdNoThrow();
    }
}

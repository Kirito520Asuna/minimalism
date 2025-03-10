package com.minimalism.common.service.impl;

import com.minimalism.common.service.CommonUserService;
import com.minimalism.config.shiro.ShiroConfig;
import com.minimalism.constant.ExpressionConstants;
import com.minimalism.utils.shiro.SecurityContextUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

/**
 * @Author yan
 * @Date 2025/3/7 0:41:29
 * @Description
 */
@Service
//@ConditionalOnExpression(ExpressionConstants.authorizationShiroAllExpression)
@ConditionalOnBean(ShiroConfig.class)
public class ShiroUserServiceImpl implements CommonUserService {
    @Override
    public String getUserId() {
        return SecurityContextUtil.getUserIdNoThrow();
    }
}

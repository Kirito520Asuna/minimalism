package com.minimalism.config.security.impl;

import com.minimalism.abstractinterface.AbstractSecurityConfig;
import com.minimalism.constant.ExpressionConstants;
import com.minimalism.filter.JwtFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Service;

/**
 * @Author yan
 * @Date 2024/10/28 下午12:48:22
 * @Description
 */
@Service
@ConditionalOnExpression(ExpressionConstants.filterAllExpression)
public class DefaultSecurityConfigImpl implements AbstractSecurityConfig {
    @Override
    public void addFilterBeforeList(HttpSecurity http) {
        AbstractSecurityConfig.super.addFilterBeforeList(http);
    }
    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter();
    }
}

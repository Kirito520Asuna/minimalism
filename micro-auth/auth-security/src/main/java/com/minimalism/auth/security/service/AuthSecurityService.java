package com.minimalism.auth.security.service;

import com.minimalism.auth.security.auth.AuthSecurityInterceptor;
import com.minimalism.auth.security.auth.JwtAuthSecurityFilter;
import com.minimalism.auth.security.utils.EncodePasswordUtils;
import com.minimalism.basic.core.abs.auth.AbsAuthFilter;
import com.minimalism.basic.core.abs.auth.AbsAuthInterceptor;
import com.minimalism.basic.core.abs.auth.service.AbsAuthService;

/**
 * @Author yan
 * @Date 2025/6/12 23:27:01
 * @Description
 */
public interface AuthSecurityService extends AbsAuthService {
    @Override
    default AbsAuthInterceptor getAuthInterceptor() {
        return new AuthSecurityInterceptor();
    }

    @Override
    default AbsAuthFilter getAuthFiler() {
        return new JwtAuthSecurityFilter();
    }

    @Override
    default boolean matchPassword(String plainPassword, String encodedPassword) {
        return EncodePasswordUtils.matchPassword(plainPassword, encodedPassword);
    }

    @Override
    default String encodePassword(String plainPassword) {
        return EncodePasswordUtils.encodePassword(plainPassword);
    }
}

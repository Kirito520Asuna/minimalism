package com.minimalism.security.abs;

import com.minimalism.common_code.abs.service.filter.AbsAuthFiler;

/**
 * @Author yan
 * @Date 2025/3/10 3:33:22
 * @Description
 */
public interface AuthorizationFilter extends AbsAuthFiler, AbsAuthorizationSecurity {
    @Override
    default void init() {
        AbsAuthFiler.super.init();
    }

}

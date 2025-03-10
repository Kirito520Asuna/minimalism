package com.minimalism.abstractinterface;

import com.minimalism.abstractinterface.service.filter.AbstractAuthFiler;

/**
 * @Author yan
 * @Date 2025/3/10 3:33:22
 * @Description
 */
public interface AuthorizationFilter extends AbstractAuthFiler, AbstractAuthorizationSecurity {
    @Override
    default void init() {
        AbstractAuthFiler.super.init();
    }

}

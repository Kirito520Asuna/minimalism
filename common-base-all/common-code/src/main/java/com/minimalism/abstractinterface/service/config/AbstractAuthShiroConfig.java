package com.minimalism.abstractinterface.service.config;

/**
 * @Author yan
 * @Date 2025/3/10 17:58:56
 * @Description
 */
public interface AbstractAuthShiroConfig extends AbstractAuthorizationConfig {
    @Override
    default void init() {
        debug("[Bean]-[AuthShiro]-[init]::[{}]",getAClassName());
    }
}

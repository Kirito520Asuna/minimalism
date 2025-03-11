package com.minimalism.abstractinterface.service.config;

import com.minimalism.abstractinterface.bean.AbstractBean;

import javax.annotation.PostConstruct;

/**
 * @Author yan
 * @Date 2025/3/10 4:04:43
 * @Description
 */
public interface AbstractAuthorizationConfig extends BeanConfig {
    @Override
    @PostConstruct
    default void init() {
        debug("[Bean]-[Auth]-[config]-[init]::[{}]",getAClassName());
    }
}

package com.minimalism.basic.core.abs.auth.config;

import com.minimalism.aop.abs.bean.AbsBean;
import javax.annotation.PostConstruct;


/**
 * @Author yan
 * @Date 2025/3/10 4:04:43
 * @Description
 */
public interface AbsAuthorizationConfig extends AbsBean {
    @Override
    @PostConstruct
    default void init() {
        log().debug("[Auth]-[Config]-[init]::[{}]",getAClassName());
    }
}

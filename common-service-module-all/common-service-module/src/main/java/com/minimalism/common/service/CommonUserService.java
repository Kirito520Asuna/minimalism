package com.minimalism.common.service;

import com.minimalism.abstractinterface.bean.AbstractBean;

/**
 * @Author yan
 * @Date 2025/3/7 0:39:57
 * @Description
 */
public interface CommonUserService extends AbstractBean {
    String getUserId();

    @Override
    default void init() {
        debug("[Common]-[Auth]-[init] {}",getClass().getName());
    }
}

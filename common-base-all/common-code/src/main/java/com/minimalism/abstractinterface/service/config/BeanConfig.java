package com.minimalism.abstractinterface.service.config;

import com.minimalism.abstractinterface.bean.AbstractBean;

import javax.annotation.PostConstruct;

/**
 * @Author yan
 * @Date 2025/3/10 20:04:00
 * @Description
 */
public interface BeanConfig extends AbstractBean {

    @Override
    @PostConstruct
    default void init() {
        debug("[init]-[BeanConfig] {}", getAClass().getName());
    }
}

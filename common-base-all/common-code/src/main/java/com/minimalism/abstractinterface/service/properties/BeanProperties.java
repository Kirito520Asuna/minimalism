package com.minimalism.abstractinterface.service.properties;

import com.minimalism.aop.abs.bean.AbsBean;

import javax.annotation.PostConstruct;

/**
 * @Author yan
 * @Date 2025/3/10 19:52:45
 * @Description
 */
public interface BeanProperties extends AbsBean {
    @Override
    @PostConstruct
    default void init() {
        debug("[init]-[Properties]-[Config]::[{}]",getAClassName());
    }
}

package com.minimalism.abstractinterface.service.filter;

import com.minimalism.abstractinterface.AbstractApiSign;

import javax.annotation.PostConstruct;

/**
 * @Author yan
 * @Date 2025/3/10 3:31:21
 * @Description
 */
public interface AbstractApiFiler extends AbstractApiSign {
    @Override
    @PostConstruct
    default void init() {
        debug("[Bean]-[ApiFiler]-[init] {}",getClass().getName());
    }
}

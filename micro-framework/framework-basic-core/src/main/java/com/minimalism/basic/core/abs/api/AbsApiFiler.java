package com.minimalism.basic.core.abs.api;


import com.minimalism.basic.core.abs.api.core.AbsApiSign;
import com.minimalism.basic.core.abs.filter.AbsCommonFilter;
import com.minimalism.basic.core.abs.order.FilterOrderConstants;
import javax.annotation.PostConstruct;
import javax.servlet.*;

import java.io.IOException;

/**
 * @Author yan
 * @Date 2025/3/10 3:31:21
 * @Description
 */
public interface AbsApiFiler extends AbsApiSign, AbsCommonFilter {
    @Override
    default int getOrder() {
        return FilterOrderConstants.ApiOrder;
    }

    @Override
    @PostConstruct
    default void init() {
       log().debug("[Bean]-[ApiFiler]-[init]::[{}]",getAClassName());
    }

    default void executeLog() {
        log().debug("class:{}, execute",getAClassName());
    }

    @Override
    default void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        executeLog();
        AbsCommonFilter.super.doFilter(servletRequest, servletResponse, filterChain);
    }
}

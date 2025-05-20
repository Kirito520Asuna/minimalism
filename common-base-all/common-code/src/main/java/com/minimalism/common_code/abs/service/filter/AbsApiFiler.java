package com.minimalism.common_code.abs.service.filter;

import com.minimalism.common_code.abs.AbsApiSign;
import com.minimalism.common_code.abs.order.FilerOrderConstants;

import javax.annotation.PostConstruct;

/**
 * @Author yan
 * @Date 2025/3/10 3:31:21
 * @Description
 */
public interface AbsApiFiler extends AbsApiSign, AbstractFilerOrder {
    @Override
    default int getOrder() {
        return FilerOrderConstants.ApiOrder;
    }

    @Override
    @PostConstruct
    default void init() {
        debug("[Bean]-[ApiFiler]-[init]::[{}]: ",getAClassName());
    }
}

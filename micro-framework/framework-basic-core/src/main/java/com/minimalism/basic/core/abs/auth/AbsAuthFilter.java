package com.minimalism.basic.core.abs.auth;

import com.minimalism.basic.core.abs.auth.core.AbsAuthorization;
import com.minimalism.basic.core.abs.filter.AbsCommonFilter;
import com.minimalism.basic.core.abs.order.FilterOrderConstants;

/**
 * @Author yan
 * @Date 2025/3/10 3:31:21
 * @Description
 */
public interface AbsAuthFilter extends AbsAuthorization, AbsCommonFilter {
    @Override
    default void init() {
        log().debug("[Bean]-[Authorization]-[Filter]-[init]::[{}]: ", getAClassName());
    }

    default void executeLog() {
        log().debug("class:{}, execute",getAClassName());
    }

    @Override
    default int getOrder() {
        return FilterOrderConstants.JwtOrder;
    }
}

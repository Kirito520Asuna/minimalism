package com.minimalism.abstractinterface.service.filter;

import com.minimalism.abstractinterface.order.FilerOrderConstants;
import com.minimalism.abstractinterface.service.AbstractAuthorization;

/**
 * @Author yan
 * @Date 2025/3/10 3:31:21
 * @Description
 */
public interface AbstractAuthFiler extends AbstractAuthorization, AbstractFilerOrder {
    @Override
    default void init() {
        debug("[Bean]-[Authorization]-[Filter]-[init]::[{}]: ", getAClassName());
    }

    @Override
    default int getOrder() {
        return FilerOrderConstants.JwtOrder;
    }
}

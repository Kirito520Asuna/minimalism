package com.minimalism.abstractinterface.service.filter;

import com.minimalism.abstractinterface.order.FilerOrderConstants;
import com.minimalism.abstractinterface.service.AbsAuthorization;

/**
 * @Author yan
 * @Date 2025/3/10 3:31:21
 * @Description
 */
public interface AbsAuthFiler extends AbsAuthorization, AbstractFilerOrder {
    @Override
    default void init() {
        debug("[Bean]-[Authorization]-[Filter]-[init]::[{}]: ", getAClassName());
    }

    @Override
    default int getOrder() {
        return FilerOrderConstants.JwtOrder;
    }
}

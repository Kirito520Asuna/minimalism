package com.minimalism.common_code.abs.service.filter;

import com.minimalism.common_code.abs.order.FilerOrderConstants;
import org.springframework.core.Ordered;

/**
 * @Author yan
 * @Date 2025/5/6 09:09:55
 * @Description
 */
public interface AbstractFilerOrder extends Ordered {
    @Override
    default int getOrder() {
        return FilerOrderConstants.BaseOrder;
    }
}

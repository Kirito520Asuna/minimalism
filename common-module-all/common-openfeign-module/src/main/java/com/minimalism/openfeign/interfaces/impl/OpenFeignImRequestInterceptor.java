package com.minimalism.openfeign.interfaces.impl;

import com.minimalism.openfeign.factory.AbstractEnum;
import com.minimalism.openfeign.interfaces.AbstractRequestInterceptor;

/**
 * @Author yan
 * @Date 2024/10/2 下午12:41:14
 * @Description
 */
public class OpenFeignImRequestInterceptor implements AbstractRequestInterceptor {
    public AbstractEnum getAbstractEnum() {
        return AbstractEnum.IM;
    }
}

package com.minimalism.openfeign.interfaces.impl;

import com.minimalism.openfeign.factory.AbsEnum;
import com.minimalism.openfeign.interfaces.AbsRequestInterceptor;

/**
 * @Author yan
 * @Date 2024/10/2 下午12:41:14
 * @Description
 */
public class OpenFeignImRequestInterceptor implements AbsRequestInterceptor {
    public AbsEnum getAbstractEnum() {
        return AbsEnum.IM;
    }
}

package com.minimalism.openfeign.factory.interfaces.impl;

import com.minimalism.openfeign.factory.AbstractEnum;
import com.minimalism.openfeign.factory.interfaces.AbstractClient;

/**
 * @Author yan
 * @Date 2024/5/14 0014 13:26
 * @Description
 */
public class AbstractClientFallback implements AbstractClient {
    @Override
    public boolean support(AbstractEnum abstractEnum) {
        return AbstractClient.super.support(abstractEnum);
    }
}

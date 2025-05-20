package com.minimalism.openfeign.factory.interfaces.impl;

import com.minimalism.openfeign.factory.AbsEnum;
import com.minimalism.openfeign.factory.interfaces.AbsClient;

/**
 * @Author yan
 * @Date 2024/5/14 0014 13:26
 * @Description
 */
public class AbsClientFallback implements AbsClient {
    @Override
    public boolean support(AbsEnum absEnum) {
        return AbsClient.super.support(absEnum);
    }
}

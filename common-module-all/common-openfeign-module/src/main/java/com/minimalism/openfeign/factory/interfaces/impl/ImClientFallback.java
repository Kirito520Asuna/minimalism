package com.minimalism.openfeign.factory.interfaces.impl;

import com.minimalism.openfeign.factory.AbstractEnum;
import com.minimalism.openfeign.factory.interfaces.ImClient;
import com.minimalism.pojo.openfeign.OpenfeignChatMessage;
import com.minimalism.result.Result;

/**
 * @Author yan
 * @Date 2024/5/14 0014 13:26
 * @Description
 */
public class ImClientFallback implements ImClient {
    @Override
    public boolean support(AbstractEnum abstractEnum) {
        return ImClient.super.support(abstractEnum);
    }
    @Override
    public Result sendMessage(OpenfeignChatMessage openfeignChatMessage) {
        return SERVICE_BUSYNESS;
    }


}

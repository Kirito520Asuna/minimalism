package com.minimalism.basic.openfeign.factory.interfaces;

import com.minimalism.basic.enums.ApiCode;
import com.minimalism.basic.result.Result;
import com.minimalism.basic.openfeign.factory.AbsEnum;

/**
 * @Author yan
 * @Date 2024/5/14 0014 11:43
 * @Description
 */
/*@FeignClient(name = "AbstractClient", path = "/abstract", fallback = AbstractClientFallback.class,
        configuration = FeignClientConfiguration.class)*/
public interface AbsClient {
    Result SERVICE_BUSYNESS = Result.result(ApiCode.SERVICE_BUSYNESS);
    default boolean support(AbsEnum absEnum) {
        return false;
    }

}

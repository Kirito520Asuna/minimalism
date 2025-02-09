package com.minimalism.openfeign.factory.interfaces;

import com.minimalism.enums.ApiCode;
import com.minimalism.openfeign.factory.AbstractEnum;
import com.minimalism.result.Result;

/**
 * @Author yan
 * @Date 2024/5/14 0014 11:43
 * @Description
 */
/*@FeignClient(name = "AbstractClient", path = "/abstract", fallback = AbstractClientFallback.class,
        configuration = FeignClientConfiguration.class)*/
public interface AbstractClient {
    Result SERVICE_BUSYNESS = Result.result(ApiCode.SERVICE_BUSYNESS);
    default boolean support(AbstractEnum abstractEnum) {
        return false;
    }

}

package com.minimalism.openfeign.factory.interfaces;

import cn.hutool.core.collection.CollUtil;
import com.minimalism.openfeign.factory.AbsEnum;
import com.minimalism.openfeign.factory.interfaces.impl.OrderClientFallback;
import com.minimalism.openfeign.interfaces.impl.OpenFeignDefaultClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.List;

/**
 * @author yan
 * @date 2024/5/21 6:44
 */
@FeignClient(name = "order", path = "/order", fallback = OrderClientFallback.class
        , configuration = OpenFeignDefaultClientConfiguration.class
)
public interface OrderClient extends AbsClient {
    List<AbsEnum> orderClientList = CollUtil.newArrayList(AbsEnum.ORDER);
    @Override
    default boolean support(AbsEnum absEnum) {
        return orderClientList.contains(absEnum);
    }
}

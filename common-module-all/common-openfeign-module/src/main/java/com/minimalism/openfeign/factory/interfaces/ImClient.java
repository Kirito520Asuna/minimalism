package com.minimalism.openfeign.factory.interfaces;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.annotation.JsonView;
import com.minimalism.openfeign.factory.AbsEnum;
import com.minimalism.openfeign.factory.interfaces.impl.OrderClientFallback;
import com.minimalism.openfeign.interfaces.impl.OpenFeignDefaultClientConfiguration;
import com.minimalism.common_code.pojo.openfeign.OpenfeignChatMessage;
import com.minimalism.base.result.Result;
import com.minimalism.base.view.BaseJsonView;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author yan
 * @date 2024/5/21 6:44
 */
@FeignClient(name = "im", path = "/im", fallback = OrderClientFallback.class
        , configuration = OpenFeignDefaultClientConfiguration.class
)
public interface ImClient extends AbsClient {
    List<AbsEnum> imClientList = CollUtil.newArrayList(AbsEnum.IM);
    @Override
    default boolean support(AbsEnum absEnum) {
        return imClientList.contains(absEnum);
    }

    @PostMapping(value = "/api/chat/send/message")
    Result sendMessage(@JsonView(value = {BaseJsonView.SendMessageView.class})
                       @RequestBody OpenfeignChatMessage openfeignChatMessage);

}

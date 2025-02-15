package com.minimalism.openfeign.factory.interfaces;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.annotation.JsonView;
import com.minimalism.openfeign.factory.AbstractEnum;
import com.minimalism.openfeign.factory.interfaces.impl.OrderClientFallback;
import com.minimalism.openfeign.interfaces.impl.OpenFeignDefaultClientConfiguration;
import com.minimalism.pojo.openfeign.OpenfeignChatMessage;
import com.minimalism.result.Result;
import com.minimalism.view.BaseJsonView;
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
public interface ImClient extends AbstractClient{
    List<AbstractEnum> imClientList = CollUtil.newArrayList(AbstractEnum.IM);
    @Override
    default boolean support(AbstractEnum abstractEnum) {
        return imClientList.contains(abstractEnum);
    }

    @PostMapping(value = "/api/chat/send/message")
    Result sendMessage(@JsonView(value = {BaseJsonView.SendMessageView.class})
                       @RequestBody OpenfeignChatMessage openfeignChatMessage);

}

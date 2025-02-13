package com.minimalism.openfeign.interfaces.impl;

import com.minimalism.openfeign.factory.AbstractEnum;
import com.minimalism.openfeign.interfaces.AbstractOpenFeignClientConfiguration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author yan
 * @Date 2025/2/13 4:05:53
 * @Description
 */
@Getter
@Slf4j
@SuppressWarnings("SpringFacetCodeInspection")
@Component
public class OpenFeignImClientConfiguration implements AbstractOpenFeignClientConfiguration {
    @Override
    public AbstractEnum getAbstractEnum() {
        return AbstractEnum.IM;
    }
}

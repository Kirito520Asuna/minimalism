package com.minimalism.openfeign.interfaces.impl;

import com.minimalism.openfeign.factory.AbsEnum;
import com.minimalism.openfeign.interfaces.AbsOpenFeignClientConfiguration;
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
public class OpenFeignImClientConfiguration implements AbsOpenFeignClientConfiguration {
    @Override
    public AbsEnum getAbstractEnum() {
        return AbsEnum.IM;
    }
}

package com.minimalism.openfeign.interfaces.impl;

import com.minimalism.openfeign.factory.AbsEnum;
import com.minimalism.openfeign.interfaces.AbsOpenFeignClientConfiguration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Author yan
 * @Date 2024/5/14 0014 10:04
 * @Description
 */
@Getter
@Slf4j
@SuppressWarnings("SpringFacetCodeInspection")
@Component
public class OpenFeignDefaultClientConfiguration implements AbsOpenFeignClientConfiguration {

    @Override
    public AbsEnum getAbstractEnum() {
        return AbsEnum.DEFAULT;
    }

    @Bean("OpenFeignDefaultRequestInterceptor")
    public OpenFeignDefaultRequestInterceptor feignRequestInterceptor() {
        return new OpenFeignDefaultRequestInterceptor();
    }

    @Override
    @PostConstruct
    public void init() {
        AbsOpenFeignClientConfiguration.super.init();
        OPEN_MAP.put(AbsEnum.DEFAULT, getClass());
        info("openMap:{}", OPEN_MAP);
    }

}

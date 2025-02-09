package com.minimalism.openfeign.interfaces.impl;

import com.minimalism.openfeign.factory.AbstractEnum;
import com.minimalism.openfeign.interfaces.AbstractOpenFeignClientConfiguration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
public class OpenFeignDefaultClientConfiguration implements AbstractOpenFeignClientConfiguration {

    @Override
    public AbstractEnum getAbstractEnum() {
        return AbstractEnum.DEFAULT;
    }

    @Bean("OpenFeignDefaultRequestInterceptor")
    public OpenFeignDefaultRequestInterceptor feignRequestInterceptor() {
        return new OpenFeignDefaultRequestInterceptor();
    }

    @Override
    @PostConstruct
    public void init() {
        AbstractOpenFeignClientConfiguration.super.init();
        OPEN_MAP.put(AbstractEnum.DEFAULT, getClass());
        info("openMap:{}", OPEN_MAP);
    }

}

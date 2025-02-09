package com.minimalism.openfeign.interfaces;

import com.google.common.collect.Maps;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.openfeign.factory.AbstractEnum;
import feign.Target;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.URI;
import java.util.Map;

/**
 * @author yan
 * @date 2024/5/21 6:58
 */
public interface AbstractOpenFeignClientConfiguration extends AbstractBean {
    Map<AbstractEnum,Class<? extends AbstractOpenFeignClientConfiguration>> OPEN_MAP = Maps.newConcurrentMap();

    /**
     * Feign url 重构
     *
     * @param feignTarget
     * @param originalUri
     * @return
     */
    default String doReconstructURL(Target<?> feignTarget, URI originalUri) {
        DefaultUriBuilderFactory defaultUriBuilderFactory = new DefaultUriBuilderFactory();
        defaultUriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        return defaultUriBuilderFactory.uriString(new StringBuffer(feignTarget.url()).append(originalUri).toString()).replaceQuery(null).build().toString();
    }

    default AbstractEnum getAbstractEnum(){
        return AbstractEnum.DEFAULT;
    }

    default String getApiSalt(){
        return getAbstractEnum().getApiSalt().getSalt();
    }
    default String getSignAsName(){
        return getAbstractEnum().getApiSalt().getSignAsName();
    }

    default String getTimestampAsName(){
        return getAbstractEnum().getApiSalt().getTimestampAsName();
    }

}

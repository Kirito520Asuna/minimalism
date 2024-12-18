package com.minimalism.filter.bean;

import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.filter.ApiFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @Author yan
 * @Date 2024/10/27 下午10:20:37
 * @Description
 */
@Component
@ConditionalOnExpression("${common.jwt.openFilter:true}&&!${common.jwt.openInterceptor:false}")
public class FilterBean implements AbstractBean {
    @Override
    public void init() {
        AbstractBean.super.init();
        getLogger().info("common.jwt.openFilter:{} && !common.jwt.openInterceptor:{}",true,false);

    }

    @Bean
    //@ConditionalOnExpression("${common.jwt.openFilter:true}&&!${common.jwt.openInterceptor:false}")
    public ApiFilter apiFilter() {
        return new ApiFilter();
    }
}

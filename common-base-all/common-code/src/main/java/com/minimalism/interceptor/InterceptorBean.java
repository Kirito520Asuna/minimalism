package com.minimalism.interceptor;

import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.abstractinterface.service.AbstractApiFiler;
import com.minimalism.abstractinterface.service.AbstractAuthFiler;
import com.minimalism.interceptor.Impl.DefaultApiInterceptor;
import com.minimalism.interceptor.Impl.DefaultInterceptor;
import com.minimalism.interceptor.Impl.DefaultLogInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @Author yan
 * @Date 2024/10/28 下午12:32:11
 * @Description
 */
@Component
@ConditionalOnExpression("${common.jwt.openInterceptor:false}&&!${common.jwt.openFilter:true}")
public class InterceptorBean implements AbstractBean {
    @Override
    public void init() {
        AbstractBean.super.init();
        info("common.jwt.openInterceptor:{} && !common.jwt.openFilter:{}",true,false);
    }

    @Bean
    public DefaultInterceptor defaultInterceptor(){
        return new DefaultInterceptor();
    }
    @Bean
    @ConditionalOnMissingBean(AbstractApiFiler.class)
    public AbstractApiInterceptor defaultApiInterceptor(){
        return new DefaultApiInterceptor();
    }
    @Bean
    @ConditionalOnMissingBean(AbstractAuthFiler.class)
    public AbstractLogInInterceptor defaultLogInterceptor(){
        return new DefaultLogInterceptor();
    }
}

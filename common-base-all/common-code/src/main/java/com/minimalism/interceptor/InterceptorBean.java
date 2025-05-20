package com.minimalism.interceptor;

import com.minimalism.aop.abs.bean.AbsBean;
import com.minimalism.abstractinterface.service.filter.AbsApiFiler;
import com.minimalism.abstractinterface.service.filter.AbsAuthFiler;
import com.minimalism.interceptor.Impl.DefaultApiInterceptor;
import com.minimalism.interceptor.Impl.DefaultInterceptor;
import com.minimalism.interceptor.Impl.DefaultLogInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @Author yan
 * @Date 2024/10/28 下午12:32:11
 * @Description
 */
@Component
//@ConditionalOnExpression("${common.openInterceptor:false}&&!${common.openFilter:true}")
@ConditionalOnMissingBean({AbsApiFiler.class, AbsAuthFiler.class})
public class InterceptorBean implements AbsBean {
    @Override
    public void init() {
        debug("[Bean]-[Interceptor]-[init] {}",getClass().getName());
    }

    @Bean
    public DefaultInterceptor defaultInterceptor(){
        return new DefaultInterceptor();
    }
    @Bean
    @ConditionalOnMissingBean(AbsApiFiler.class)
    public AbsApiInterceptor defaultApiInterceptor(){
        return new DefaultApiInterceptor();
    }
    @Bean
    @ConditionalOnMissingBean(AbsAuthFiler.class)
    public AbsLogInInterceptor defaultLogInterceptor(){
        return new DefaultLogInterceptor();
    }
}

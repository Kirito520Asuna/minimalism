package com.minimalism.interceptor;

import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.abstractinterface.service.filter.AbstractApiFiler;
import com.minimalism.abstractinterface.service.filter.AbstractAuthFiler;
import com.minimalism.interceptor.Impl.DefaultApiInterceptor;
import com.minimalism.interceptor.Impl.DefaultInterceptor;
import com.minimalism.interceptor.Impl.DefaultLogInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @Author yan
 * @Date 2024/10/28 下午12:32:11
 * @Description
 */
@Component
//@ConditionalOnExpression("${common.openInterceptor:false}&&!${common.openFilter:true}")
@ConditionalOnMissingBean({AbstractApiFiler.class,AbstractAuthFiler.class})
public class InterceptorBean implements AbstractBean {
    @Override
    public void init() {
        debug("[Bean]-[Interceptor]-[init] {}",getClass().getName());
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

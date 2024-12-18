package com.minimalism.interceptor;

import com.minimalism.abstractinterface.AbstractInterceptor;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.interceptor.Impl.DefaultApiInterceptor;
import com.minimalism.interceptor.Impl.DefaultInterceptor;
import com.minimalism.interceptor.Impl.DefaultLogInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @Author yan
 * @Date 2024/10/28 下午12:32:11
 * @Description
 */
@Component @Slf4j
@ConditionalOnExpression("${common.jwt.openInterceptor:false}&&!${common.jwt.openFilter:true}")
public class InterceptorBean implements AbstractBean {
    @Override
    public void init() {
        AbstractBean.super.init();
        log.info("common.jwt.openInterceptor:{} && !common.jwt.openFilter:{}",true,false);
    }

    @Bean
    //@ConditionalOnExpression("${common.jwt.openInterceptor:false}&&!${common.jwt.openFilter:true}")
    public DefaultInterceptor defaultInterceptor(){
        return new DefaultInterceptor();
    }
    @Bean
    //@ConditionalOnExpression("${common.jwt.openInterceptor:false}&&!${common.jwt.openFilter:true}")
    public AbstractApiInterceptor defaultApiInterceptor(){
        return new DefaultApiInterceptor();
    }
    @Bean
    //@ConditionalOnExpression("${common.jwt.openInterceptor:false}&&!${common.jwt.openFilter:true}")
    public AbstractLogInInterceptor defaultLogInterceptor(){
        return new DefaultLogInterceptor();
    }
}

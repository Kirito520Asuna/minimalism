package com.minimalism.auth.security.config.bean;

import com.minimalism.auth.security.abs.AbsSecurityConfig;
import com.minimalism.auth.security.abs.SimpleSecurityConfig;
import com.minimalism.auth.security.auth.SecurityExpressionRoot;
import com.minimalism.basic.core.abs.auth.core.AbsSecurityExpressionRoot;
import com.minimalism.basic.core.abs.bean.AbstractSecurityBean;

import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @Author yan
 * @Date 2025/6/11 19:12:34
 * @Description
 */
@Slf4j
@AutoConfigureAfter(BeanBeforeSecurityConfig.class)
@Configuration
public class BeanSecurityConfig implements AbstractSecurityBean {

    @PostConstruct
    public void init() {
        log().info("==> Security <== class:{}",getAClassName());
    }

    @Bean
    @ConditionalOnMissingBean(AbsSecurityConfig.class)
    public AbsSecurityConfig authSecurityConfig() {
        log().debug("==> authSecurityConfig <== class:{}",getAClassName());
        return new SimpleSecurityConfig();
    }

    //@Bean
    //@ConditionalOnExpression("${config.auth.security.enable:true}")
    //public SecurityConfig securityConfig() {
    //    return new SecurityConfig();
    //}

    @Bean("auth")
    //@ConditionalOnBean(SecurityConfig.class)
    @ConditionalOnMissingBean(AbsSecurityExpressionRoot.class)
    public AbsSecurityExpressionRoot securityExpressionRoot(){
        return new SecurityExpressionRoot();
    }

}

package com.minimalism.auth.security.config.bean;

import com.minimalism.auth.security.service.AbsUserDetailsService;
import com.minimalism.auth.security.service.impl.SimpleAuthSecurityService;
import com.minimalism.auth.security.service.impl.SimpleLoginSecurityService;
import com.minimalism.auth.security.service.impl.SimpleUserDetailsSecurityService;
import com.minimalism.basic.core.abs.auth.service.AbsAuthService;
import com.minimalism.basic.core.abs.auth.service.AbstractLoginService;
import com.minimalism.basic.core.abs.auth.service.AbstractUserDetailsService;
import com.minimalism.basic.core.config.bean.BeanConfig;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yan
 * @Date 2025/6/18 18:00:45
 * @Description
 */
@Slf4j
@Configuration
@AutoConfigureBefore(BeanSecurityConfig.class)
public class BeanBeforeSecurityConfig extends BeanConfig {

    @PostConstruct
    public void init() {
        log().info("==> Security <== class:{}", getAClassName());
    }

    @Bean
    @Override
    @ConditionalOnMissingBean(AbsAuthService.class)
    public AbsAuthService authService() {
        return new SimpleAuthSecurityService();
    }


    @Bean
    //@ConditionalOnBean(SecurityConfig.class)
    @ConditionalOnMissingBean(AbstractLoginService.class)
    public AbstractLoginService authLoginService() {
        return new SimpleLoginSecurityService();
    }

    @Bean
    //@ConditionalOnBean(SecurityConfig.class)
    @ConditionalOnMissingBean(AbstractUserDetailsService.class)
    public AbsUserDetailsService authUserDetailsService() {
        log().debug("class:{} ==> authUserDetailsService <==",getAClassName());
        return new SimpleUserDetailsSecurityService();
    }


}

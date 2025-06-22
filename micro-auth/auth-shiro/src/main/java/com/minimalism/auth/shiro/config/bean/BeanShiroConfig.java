package com.minimalism.auth.shiro.config.bean;

import com.minimalism.auth.shiro.config.ShiroConfig;
import com.minimalism.basic.core.abs.auth.service.AbsAuthService;
import com.minimalism.basic.core.abs.bean.AbstractShiroBean;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yan
 * @Date 2025/6/13 23:34:39
 * @Description
 */
@Slf4j
@AutoConfigureAfter(BeanBeforeShiroConfig.class)
@Configuration
public class BeanShiroConfig implements AbstractShiroBean {
    @PostConstruct
    public void init() {
        log().info("==> Shiro <== class:{}", getAClassName());
    }

    @Bean
    @ConditionalOnBean(AbsAuthService.class)
    @ConditionalOnExpression("${config.auth.shiro.enable:true}")
    public ShiroConfig shiroConfig() {
        return new ShiroConfig();
    }


}

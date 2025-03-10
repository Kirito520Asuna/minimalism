package com.minimalism.interceptor.config;

import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.service.filter.AbstractApiFiler;
import com.minimalism.abstractinterface.service.filter.AbstractAuthFiler;
import com.minimalism.config.JwtConfig;
import com.minimalism.interceptor.AbstractWebConfigurerAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author yan
 * @Date 2024/5/14 0014 14:54
 * @Description
 */
@ConditionalOnMissingBean(value = {AbstractApiFiler.class, AbstractAuthFiler.class})
@ConditionalOnBean(JwtConfig.class)
@Configuration
public class WebConfigurerAdapter implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        JwtConfig jwtConfig = SpringUtil.getBean(JwtConfig.class);
        Boolean openInterceptor = jwtConfig.getOpenInterceptor();
        if (openInterceptor) {
            SpringUtil.getBean(AbstractWebConfigurerAdapter.class)
                    .initInterceptors(registry);
        }
    }
}

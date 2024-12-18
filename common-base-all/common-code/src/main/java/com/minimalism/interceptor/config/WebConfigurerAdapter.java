package com.minimalism.interceptor.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.config.JwtConfig;
import com.minimalism.interceptor.AbstractWebConfigurerAdapter;
import com.minimalism.interceptor.Impl.DefaultApiInterceptor;
import com.minimalism.interceptor.Impl.DefaultInterceptor;
import com.minimalism.interceptor.Impl.DefaultLogInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author yan
 * @Date 2024/5/14 0014 14:54
 * @Description
 */
@Configuration
@Slf4j
public class WebConfigurerAdapter implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        JwtConfig jwtConfig = SpringUtil.getBean(JwtConfig.class);
        Boolean openInterceptor =
                ObjectUtil.defaultIfNull(jwtConfig.getOpenInterceptor(), false)
                && !
                ObjectUtil.defaultIfNull(jwtConfig.getOpenFilter(), true)
                ;
        if (openInterceptor) {
            SpringUtil.getBean(AbstractWebConfigurerAdapter.class)
                    .initInterceptors(registry);
        }
    }
}

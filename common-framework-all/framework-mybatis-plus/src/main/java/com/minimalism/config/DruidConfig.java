package com.minimalism.config;

import com.minimalism.abstractinterface.config.AbstractDruidConfig;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 默认地址http://127.0.0.1:8080/druid
 *
 * @Author yan
 * @Date 2023/4/28 0028 12:16
 * @Description Druid配置
 */
@Configuration
public class DruidConfig implements AbstractDruidConfig {
    /**
     * 注册servletRegistrationBean
     */
    @Bean @Override
    public ServletRegistrationBean servletRegistrationBean() {
        return AbstractDruidConfig.super.servletRegistrationBean();
    }

    /**
     * 注册filterRegistrationBean
     */
    @Bean @Override
    public FilterRegistrationBean filterRegistrationBean() {
        return AbstractDruidConfig.super.filterRegistrationBean();
    }
}

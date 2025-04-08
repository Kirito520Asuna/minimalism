package com.minimalism.filter.bean;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.constant.ExpressionConstants;
import com.minimalism.filter.ApiFilter;
import com.minimalism.filter.CorsRequestFilter;
import com.minimalism.properties.CorsProperties;
import com.minimalism.utils.object.ObjectUtils;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @Author yan
 * @Date 2024/10/27 下午10:20:37
 * @Description
 */
@Component
@ConditionalOnExpression(ExpressionConstants.filterExpression)
public class FilterBean implements AbstractBean {
    @Override
    public void init() {
      debug("[Bean]-[Filter]-[init] {}",getClass().getName());
    }

    //@Bean
    public CorsFilter corsFilter() {
        Logger log = getLogger();
        CorsProperties cors = SpringUtil.getBean(CorsProperties.class);
        String allowedOrigin = cors.getAllowedOrigin();
        String allowedOriginPattern = cors.getAllowedOriginPattern();
        Boolean allowCredentials = cors.getAllowCredentials();
        String allowedHeader = cors.getAllowedHeader();
        Long maxAge = cors.getMaxAge();

        String[] allowedMethods = cors.AllowedMethods();
        // CORS 配置
        CorsConfiguration corsConfig = new CorsConfiguration();

        if (ObjectUtils.isNotEmpty(allowedOrigin)){
            corsConfig.addAllowedOrigin(allowedOrigin);  // 允许的域
        }
        if (ObjectUtils.isNotEmpty(allowedOriginPattern)){
            corsConfig.addAllowedOriginPattern(allowedOriginPattern);  // 允许的域
        }
        for (String method : allowedMethods) {
            corsConfig.addAllowedMethod(method);  // 允许的请求方法
        }
        if (ObjectUtils.isNotEmpty(allowedHeader)){
            corsConfig.addAllowedHeader(allowedHeader);  // 允许的请求头
        }
        if (ObjectUtils.isNotEmpty(allowCredentials)){
            corsConfig.setAllowCredentials(allowCredentials);  // 是否允许凭证（cookies）
        }
        if (ObjectUtils.isNotEmpty(maxAge)){
            corsConfig.setMaxAge(maxAge);
        }
        // 配置源（UrlBasedCorsConfigurationSource）
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(ObjectUtils.defaultIfEmpty(cors.getPattern(),"/**"), corsConfig);  // 对所有路径有效

        log.info("CorsConfiguration:{}", JSONUtil.toJsonStr(corsConfig, JSONConfig.create().setIgnoreNullValue(false)));
        return new CorsFilter(source);  // 返回一个 CorsFilter 实例
    }

    @Bean
    //@ConditionalOnExpression(ExpressionConstants.corsFilterExpression)
    @ConditionalOnBean(CorsProperties.class)
    public CorsRequestFilter corsRequestFilter() {
        return new CorsRequestFilter();
    }

    @Bean
    //@ConditionalOnExpression(ExpressionConstants.filterExpression)
    @ConditionalOnBean(FilterBean.class)
    public ApiFilter apiFilter() {
        return new ApiFilter();
    }

}

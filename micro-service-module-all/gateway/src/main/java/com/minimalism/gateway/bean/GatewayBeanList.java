package com.minimalism.gateway.bean;


import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.properties.CorsGatewayProperties;
import com.minimalism.properties.CorsProperties;
import com.minimalism.utils.object.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.util.pattern.PathPatternParser;



/**
 * @Author minimalism
 * @Date 2024/11/4 下午10:35:01
 * @Description
 */
@Slf4j
@Configuration
public class GatewayBeanList implements AbstractBean {
    /**
     * 跨域配置 配合
     * {@see
     *
     * @return
     * @link org.springframework.web.cors.reactive.CorsWebFilter -- 微服务 gateway 配置跨域
     * @link com.minimalism.filter.CorsRequestFilter  -- 微服务 各个服务Filter 跨域
     */
    @Bean
    @ConditionalOnExpression("${cors.gateway.default-filter: true} &&!${cors.gateway.web-filter:false}")
    public CorsWebFilter corsWebFilter() {
        info("corsWebFilter init ...");
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true); // 允许cookies跨域
        config.addAllowedOrigin("*");// #允许向该服务器提交请求的URI，*表示全部允许，在SpringMVC中，如果设成*，会自动转成当前请求头中的Origin
        config.addAllowedHeader("*");// #允许访问的头信息,*表示全部
        config.setMaxAge(360000L);// 预检请求的缓存时间（秒），即在这个时间段里，对于相同的跨域请求不会再预检了
        config.addAllowedMethod("OPTIONS");// 允许提交请求的方法类型，*表示全部允许
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");

        org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource source =
                new org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
    @Bean
    public CorsGatewayProperties corsGatewayProperties(){
        return new CorsGatewayProperties();
    }
    /**
     * {@link com.minimalism.properties.CorsProperties}
     *
     * @return
     */
    @Bean
    @ConditionalOnExpression("${cors.gateway.web-filter:false} &&!${cors.gateway.default-filter: true}")
    public CorsProperties corsProperties(){
        return new CorsProperties();
    }
    @Bean
    @ConditionalOnExpression("${cors.gateway.web-filter:false} &&!${cors.gateway.default-filter: true}")
    public CorsWebFilter corsGatewayWebFilter() {
        CorsProperties cors = SpringUtil.getBean(CorsProperties.class);

        info("CorsProperties:{}", JSONUtil.toJsonStr(cors, JSONConfig.create().setIgnoreNullValue(false)));
        info("corsGatewayWebFilter init ...");

        String path = ObjectUtils.defaultIfEmpty(cors.getPattern(), "/**");
        String allowedOrigin = cors.getAllowedOrigin();
        String allowedHeader = cors.getAllowedHeader();
        Boolean allowCredentials = cors.getAllowCredentials();
        Long maxAge = cors.getMaxAge();
        String[] allowedMethodsList = cors.AllowedMethods();

        CorsConfiguration config = new CorsConfiguration();

        if (ObjectUtils.isNotEmpty(allowedOrigin)) {
            config.addAllowedOrigin(allowedOrigin);// #允许向该服务器提交请求的URI，*表示全部允许，在SpringMVC中，如果设成*，会自动转成当前请求头中的Origin
        }
        if (ObjectUtils.isNotEmpty(allowedHeader)) {
            config.addAllowedHeader(allowedHeader);// #允许访问的头信息,*表示全部
        }
        if (ObjectUtils.isNotEmpty(allowCredentials)) {
            config.setAllowCredentials(allowCredentials); // 允许cookies跨域
        }
        if (ObjectUtils.isNotEmpty(maxAge)) {
            config.setMaxAge(maxAge);// 预检请求的缓存时间（秒），即在这个时间段里，对于相同的跨域请求不会再预检了
        }
        for (String allowedMethod : allowedMethodsList) {
            config.addAllowedMethod(allowedMethod);// 允许提交请求的方法类型，*表示全部允许
        }

        org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource source =
                new org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration(path, config);

        return new CorsWebFilter(source);
    }

}

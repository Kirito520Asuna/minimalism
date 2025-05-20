package com.minimalism.abstractinterface;

import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.service.config.AbsAuthorizationConfig;
import com.minimalism.filter.JwtFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @Author yan
 * @Date 2024/10/28 下午12:44:53
 * @Description
 */
public interface AbsSecurityConfig extends AbsAuthorizationConfig {
    /**
     * @param http
     */
    default void addFilterBeforeList(HttpSecurity http) {
        JwtFilter jwtFilter = SpringUtil.getBean(JwtFilter.class);
        if (jwtFilter == null) {
            throw new IllegalStateException("JwtFilter is null, please make sure it's a Spring Bean");
        }
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        //ApiFilter apiFilter = SpringUtil.getBean(ApiFilter.class);
        //if (apiFilter == null) {
        //    throw new IllegalStateException("ApiFilter is null, please make sure it's a Spring Bean");
        //}
        //http.addFilterBefore(apiFilter, JwtFilter.class);
        //Environment env = SpringUtil.getBean(Environment.class);
        //Boolean corsFilte = ObjectUtils.defaultIfEmpty(env.getProperty(ExpressionConstants.corsFilte, Boolean.class), true);
        //if (corsFilte) {
            //http.addFilterBefore(SpringUtil.getBean(CorsRequestFilter.class), ApiFilter.class);
        //}
    }
}

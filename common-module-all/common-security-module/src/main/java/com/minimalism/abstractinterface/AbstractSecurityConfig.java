package com.minimalism.abstractinterface;

import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.service.config.AbstractAuthorizationConfig;
import com.minimalism.constant.ExpressionConstants;
import com.minimalism.filter.ApiFilter;
import com.minimalism.filter.CorsRequestFilter;
import com.minimalism.filter.JwtFilter;
import com.minimalism.utils.object.ObjectUtils;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @Author yan
 * @Date 2024/10/28 下午12:44:53
 * @Description
 */
public interface AbstractSecurityConfig extends AbstractAuthorizationConfig {
    /**
     * @param http
     */
    default void addFilterBeforeList(HttpSecurity http) {
        http.addFilterBefore(SpringUtil.getBean(JwtFilter.class), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(SpringUtil.getBean(ApiFilter.class), JwtFilter.class);
        Environment env = SpringUtil.getBean(Environment.class);
        Boolean corsFilte = ObjectUtils.defaultIfEmpty(env.getProperty(ExpressionConstants.corsFilte, Boolean.class), true);
        if (corsFilte) {
            http.addFilterBefore(SpringUtil.getBean(CorsRequestFilter.class), ApiFilter.class);
        }
    }
}

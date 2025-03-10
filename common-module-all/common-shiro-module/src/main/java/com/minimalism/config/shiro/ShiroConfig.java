package com.minimalism.config.shiro;

import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.AbstractShiroConfig;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.constant.ExpressionConstants;
import com.minimalism.filter.CorsRequestFilter;
import com.minimalism.filter.JwtAuthFilter;
import com.minimalism.interceptor.Impl.LogInterceptor;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.servlet.Filter;
import java.util.Map;

/**
 * @Author yan
 * @Date 2024/10/11 下午9:52:43
 * @Description
 */
@Configuration
public class ShiroConfig implements AbstractBean, AbstractShiroConfig {
    @Value("${shiro.encodePassword:false}")
    private Boolean enableEncodePassword;
    public static boolean getEnableEncodePassword() {
        Boolean encodePassword = SpringUtil.getBean(ShiroConfig.class).enableEncodePassword;
        encodePassword = encodePassword == null ? false : encodePassword;
        return encodePassword;
    }
    @Bean
    //@ConditionalOnExpression("${common.jwt.openFilter:true}&&!${common.jwt.openInterceptor:false}")
    @ConditionalOnExpression(ExpressionConstants.filterExpression)
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter();
    }
    @Bean @Primary
    @ConditionalOnExpression(ExpressionConstants.corsFilterExpression)
    public CorsRequestFilter corsRequestFilter() {
        return new CorsRequestFilter();
    }
    @Bean
    public Authorizer authorizer() {
        return AbstractShiroConfig.super.authorizer(realm());
    }
    @Bean @Primary
    @Override
    public SessionManager sessionManager() {
        return AbstractShiroConfig.super.sessionManager();
    }

    @Bean
    @Override
    public Realm realm() {
        return AbstractShiroConfig.super.realm();
    }

    //开启shiro权限注解
    @Bean
    @Override
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(WebSecurityManager webSecurityManager) {
        return AbstractShiroConfig.super.authorizationAttributeSourceAdvisor(webSecurityManager);
    }

    @Bean
    @Override
    public Authenticator authenticator() {
        return AbstractShiroConfig.super.authenticator();
    }

    //配置securityManager的实现类，变向的配置了securityManager
    @Bean
    public WebSecurityManager webSecurityManager() {
        return AbstractShiroConfig.super.securityManager(realm());
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(WebSecurityManager webSecurityManager) {
        return AbstractShiroConfig.super.shiroFilterFactoryBean(webSecurityManager);
    }

    @Override
    public Map<String, Filter> getFilters() {
        return AbstractShiroConfig.super.getFilters();
    }

    @Override
    public Map<String, String> getFilterChainDefinitionMap() {
        return AbstractShiroConfig.super.getFilterChainDefinitionMap();
    }
}

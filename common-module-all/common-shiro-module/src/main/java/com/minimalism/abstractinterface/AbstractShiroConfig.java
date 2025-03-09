package com.minimalism.abstractinterface;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.google.common.collect.Maps;
import com.minimalism.abstractinterface.service.AbstractAuthorizationConfig;
import com.minimalism.config.ApiConfig;
import com.minimalism.config.JwtConfig;
import com.minimalism.constant.ExpressionConstants;
import com.minimalism.filter.ApiFilter;
import com.minimalism.filter.CorsRequestFilter;
import com.minimalism.filter.JwtAuthFilter;
import com.minimalism.pojo.shiro.CustomRealm;
import com.minimalism.properties.CorsProperties;
import com.minimalism.utils.object.ObjectUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.springframework.core.env.Environment;

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2024/10/19 下午3:07:59
 * @Description
 */
public interface AbstractShiroConfig extends AbstractAuthorizationConfig {
    //开启shiro权限注解

    default AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(WebSecurityManager webSecurityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(webSecurityManager);
        return advisor;
    }

    default Authorizer authorizer(Realm realm) {
        ModularRealmAuthorizer authorizer = new ModularRealmAuthorizer();
        authorizer.setRealms(CollUtil.newArrayList(realm)); // 绑定 Realm
        return authorizer;
    }

    default SessionManager sessionManager() {
        DefaultSessionManager sessionManager = new DefaultSessionManager();
        // 配置 session 管理的具体行为
        sessionManager.setGlobalSessionTimeout(3600000);  // 设置全局会话超时时间（1小时）
        return sessionManager;
    }

    default Authenticator authenticator() {
        ModularRealmAuthenticator authenticator = new ModularRealmAuthenticator();
        // 如果需要，可以设置认证策略
        return authenticator;
    }

    //配置securityManager的实现类，变向的配置了securityManager
    default WebSecurityManager securityManager(Realm authRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //关联realm
        securityManager.setRealm(authRealm);
        /*
         * 关闭shiro自带的session
         */
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator sessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        sessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(sessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);
        SessionManager sessionManager = SpringUtil.getBean(SessionManager.class);
        securityManager.setSessionManager(sessionManager);

        // 绑定 SecurityManager 到 SecurityUtils
        SecurityUtils.setSecurityManager(securityManager);
        // 或者手动绑定到当前线程
        //ThreadContext.bind(securityManager);
        return securityManager;
    }

    default ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

        //关联 DefaultWebSecurityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        //添加shiro内置的过滤器
        /*
         * anon: 无需认证就可以访问
         * authc: 必须认证才能访问
         * user: 必须拥有记住我功能才能用
         * perms: 拥有对某个资源的权限才能访问
         * role: 拥有某个角色权限才能访问
         * */
        //添加过滤器
        Map<String, Filter> filters = getFilters();
        if (CollUtil.isNotEmpty(filters)) {
            shiroFilterFactoryBean.setFilters(filters);  //添加自定义的认证授权过滤器
        }

        //要拦截的路径放在map里面
        Map<String, String> filterMap = getFilterChainDefinitionMap();
        if (CollUtil.isNotEmpty(filters)) {
            shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
        }
        return shiroFilterFactoryBean;
    }

    /**
     * 获取自定义的过滤器
     *
     * @return
     */
    default Map<String, Filter> getFilters() {
        Map<String, Filter> filters = Maps.newLinkedHashMap();
        Environment env = SpringUtil.getBean(Environment.class);
        Boolean openCorsFilter = ObjectUtils.defaultIfNull(env.getProperty(ExpressionConstants.corsFilte, Boolean.class), true);

        if (openCorsFilter) {
            CorsProperties cors = SpringUtil.getBean(CorsProperties.class);
            filters.put(cors.getClass().getName(), SpringUtil.getBean(CorsRequestFilter.class));
        }

        JwtConfig jwtConfig = SpringUtil.getBean(JwtConfig.class);
        Boolean openFilter = ObjectUtil.defaultIfNull(jwtConfig.getOpenFilter(), true)
                && !ObjectUtil.defaultIfNull(jwtConfig.getOpenInterceptor(), false);
        if (openFilter) {
            filters.put("api", SpringUtil.getBean(ApiFilter.class));
            filters.put("auth", SpringUtil.getBean(JwtAuthFilter.class));
        }
        return filters;
    }

    /**
     * 获取自定义的过滤规则
     *
     * @return
     */
    default Map<String, String> getFilterChainDefinitionMap() {
        //要拦截的路径放在map里面
        Map<String, String> filterMap = new LinkedHashMap<String, String>();
        Environment env = SpringUtil.getBean(Environment.class);
        Boolean openCorsFilter = ObjectUtils.defaultIfNull(env.getProperty("cors.filter", Boolean.class), true);

        if (openCorsFilter) {
            CorsProperties cors = SpringUtil.getBean(CorsProperties.class);
            String pattern = ObjectUtils.defaultIfEmpty(cors.getPattern(), "/**");
            filterMap.put(pattern, cors.getClass().getName());
        }
        filterMap.put("/login", "anon");  //放行login接口
        filterMap.put("/logout", "anon");    //放行logout接口
        filterMap.put("/v3/api-docs/**", "anon");    //放行/v3/api-docs/**接口
        JwtConfig jwtConfig = SpringUtil.getBean(JwtConfig.class);
        Boolean openFilter = ObjectUtil.defaultIfNull(jwtConfig.getOpenFilter(), true)
                && !ObjectUtil.defaultIfNull(jwtConfig.getOpenInterceptor(), false);
        if (openFilter) {
            ApiConfig apiConfig = SpringUtil.getBean(ApiConfig.class);
            String apiPath = ObjectUtil.defaultIfNull(apiConfig.getApiPath(), "/api/**");
            filterMap.put(apiPath, "api");
            String jwtPath = ObjectUtil.defaultIfNull(jwtConfig.getJwtPath(), "/jwt/**");
            List<String> jwtPaths = Arrays.stream(jwtPath.split(",")).collect(Collectors.toList());
            jwtPaths.stream().forEach(path -> filterMap.put(path, "auth"));
            //filterMap.put("/jwt/**", "auth");    //拦截/jwt/**路径, 它自动会跑到 AuthFilter这个自定义的过滤器里面
        }
        return filterMap;
    }


    default Realm realm() {
        return new CustomRealm();
    }
}

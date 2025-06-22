package com.minimalism.auth.security.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.auth.security.abs.AbsSecurityConfig;
import com.minimalism.auth.security.config.bean.BeanSecurityConfig;
import com.minimalism.auth.security.service.AbsUserDetailsService;
import com.minimalism.basic.core.abs.auth.config.AbsAuthSecurityConfig;
import com.minimalism.basic.core.config.jwt.JwtConfig;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @Author yan
 * @Date 2025/6/12 23:24:35
 * @Description Spring Boot 3.x 的安全配置
 */
@ConditionalOnExpression("${config.auth.security.enable:true}")
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@AutoConfigureAfter(BeanSecurityConfig.class)
public class SecurityConfig extends WebSecurityConfigurerAdapter implements AbsAuthSecurityConfig {
    SecurityAutoConfiguration securityAutoConfiguration = null;

    @Override
    @PostConstruct
    public void init() {
        AbsAuthSecurityConfig.super.init();
        try {
            securityAutoConfiguration = SpringUtil.getBean(SecurityAutoConfiguration.class);
        } catch (Exception e) {
        }

        AbsUserDetailsService userDetailsService = null;
        try {
            userDetailsService = SpringUtil.getBean(AbsUserDetailsService.class);
        } catch (Exception e) {
            log().warn("未找到 AbsUserDetailsService Bean，请检查配置 error:{}", e.getMessage());
        }

        if (securityAutoConfiguration != null && userDetailsService == null) {
            Class<SecurityAutoConfiguration> classAuto = SecurityAutoConfiguration.class;

            String className = StrUtil.subBefore(classAuto.getName(), "$", false);

            log().debug(new StringBuilder("\n[Security]-[默认安全配置]-[已启用]-[用户名:user]")
                            .append("\n[自定义用户名和密码]")
                            .append("\nspring.security.user.name=自定义用户名")
                            .append("\nspring.security.user.password=自定义密码")
                            .append("\n[禁用默认安全配置]")
                            .append("\n@SpringBootApplication(exclude = { {}.class })")
                            .append("\nor")
                            .append("\nspring.autoconfigure.exclude={}\n").toString()
                    , className, className);
        }
    }

    /**
     * 密码加密器 Bean，用于加密存储密码
     *
     * @return BCryptPasswordEncoder
     */
    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        log().debug("class:{},msg:PasswordEncoder", getAClassName());
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        JwtConfig jwtConfig = null;
        try {
            jwtConfig = SpringUtil.getBean(JwtConfig.class);
        } catch (Exception e) {
            log().error("class:{},err:{}", getAClassName(), e.getMessage());
            log().warn("JwtConfig is Null");
        }
        Boolean jwtOpenFilter = jwtConfig == null ? null : jwtConfig.getOpenFilter();
        Boolean jwtOpenInterceptor = jwtConfig == null ? null : jwtConfig.getOpenInterceptor();
        Boolean openFilter = ObjectUtil.defaultIfNull(jwtOpenFilter, true)
                && !ObjectUtil.defaultIfNull(jwtOpenInterceptor, false);
        String jwtPathFinal = jwtConfig == null ? null : jwtConfig.getJwtPath();

        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry = http.cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/login", "/logout").permitAll();

        if (openFilter) {
            String jwtPath = "/jwt/**";
            jwtPath = ObjectUtil.defaultIfNull(jwtPathFinal,jwtPath);
            String[] paths = jwtPath.split(",");
            expressionInterceptUrlRegistry
                    .antMatchers(paths).authenticated(); // 以 "/jwt" 开头的请求需要认证
        }
        expressionInterceptUrlRegistry.anyRequest().permitAll(); // 其他请求允许访问

        if (securityAutoConfiguration != null) {
            http.formLogin(form -> form
                    .loginPage("/login") // 自定义登录页面
                    .loginProcessingUrl("/login") // 表单提交 URL
                    .permitAll()
            );
        }

        if (openFilter) {
            SpringUtil.getBean(AbsSecurityConfig.class).addFilterBeforeList(http);
        }
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 从数据库读取的用户进行身份认证
        AbsUserDetailsService userDetailsService = SpringUtil.getBean(AbsUserDetailsService.class);
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(SpringUtil.getBean(PasswordEncoder.class));
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
package com.minimalism.config.security;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.AbstractSecurityConfig;
import com.minimalism.config.JwtConfig;
import com.minimalism.service.AbstractUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 * @Author yan
 * @Date 2023/8/7 0007 10:23
 * @Description
 */
@Configuration
//开启权限校验
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 密码加密存储
     * BCryptPasswordEncoder 注入到容器
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        JwtConfig jwtConfig = SpringUtil.getBean(JwtConfig.class);
        Boolean openFilter =
                ObjectUtil.defaultIfNull(jwtConfig.getOpenFilter(), true)
                &&!
                ObjectUtil.defaultIfNull(jwtConfig.getOpenInterceptor(), false)
                ;

        String jwtPath = new StringBuilder("/jwt/**").toString();
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry = http.cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/login", "/logout").permitAll();

        if (openFilter) {
            jwtPath = ObjectUtil.defaultIfNull(jwtConfig.getJwtPath(), jwtPath);
            String[] paths = jwtPath.split(",");
            expressionInterceptUrlRegistry
                    .antMatchers(paths).authenticated(); // 以 "/jwt" 开头的请求需要认证
        }
        expressionInterceptUrlRegistry.anyRequest().permitAll(); // 其他请求允许访问

        //// 配置登录
        //http.formLogin()
        //        .loginPage("/login")  // 自定义登录页面URL
        //        .and()
        //        // 配置登出
        //        .logout()
        //        .logoutUrl("/logout");  // 自定义登出请求URL
        //http.addFilterBefore(SpringUtil.getBean(JwtFilter.class), UsernamePasswordAuthenticationFilter.class);
        //http.addFilterBefore(SpringUtil.getBean(ApiFilter.class), JwtFilter.class);

        if (openFilter) {
            SpringUtil.getBean(AbstractSecurityConfig.class).addFilterBeforeList(http);
        }
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 从数据库读取的用户进行身份认证
        AbstractUserDetailsService userDetailsService = SpringUtil.getBean(AbstractUserDetailsService.class);
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(SpringUtil.getBean(PasswordEncoder.class));
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


}

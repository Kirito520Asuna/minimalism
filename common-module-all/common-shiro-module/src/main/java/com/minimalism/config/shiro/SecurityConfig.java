//package com.yan.config.shiro;
//
//import cn.hutool.extra.spring.SpringUtil;
//import com.yan.filter.ApiFilter;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
///**
// * @Author yan
// * @Date 2024/10/12 上午11:46:07
// * @Description
// */
//@Configuration
////开启权限校验
////@EnableGlobalMethodSecurity(prePostEnabled = true)
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.cors().and().csrf().disable()
//                .authorizeRequests()
//                .anyRequest().permitAll(); // 其他请求允许访问
//    }
//
//}

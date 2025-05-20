package com.minimalism.shiro.interceptor.Impl;

import com.minimalism.shiro.abs.AbsAuthorizationShiro;
import com.minimalism.shiro.abs.AuthorizationFilter;
import com.minimalism.common_code.interceptor.AbsLogInInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @Author yan
 * @Date 2024/10/27 下午11:46:25
 * @Description
 */
@Service
@Primary
//@ConditionalOnExpression("${common.jwt.openInterceptor:false}&&!${common.jwt.openFilter:true}")
//@ConditionalOnExpression(ExpressionConstants.interceptorAllExpression)
@ConditionalOnMissingBean(AuthorizationFilter.class)
public class LogInterceptor implements AbsLogInInterceptor, AbsAuthorizationShiro {
    @Override
    public void checkLogin(HttpServletRequest request, HttpServletResponse response) {
        AbsAuthorizationShiro.super.checkTokenLogin(request, response);
    }
}

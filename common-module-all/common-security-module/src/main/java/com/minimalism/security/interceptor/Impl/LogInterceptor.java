package com.minimalism.security.interceptor.Impl;

import com.minimalism.security.abs.AbsAuthorizationSecurity;
import com.minimalism.security.abs.AuthorizationFilter;
import com.minimalism.interceptor.AbsLogInInterceptor;
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
public class LogInterceptor implements AbsLogInInterceptor, AbsAuthorizationSecurity {

    @Override
    public void checkLogin(HttpServletRequest request, HttpServletResponse response) {
        AbsAuthorizationSecurity.super.checkTokenLogin(request, response);
    }

}

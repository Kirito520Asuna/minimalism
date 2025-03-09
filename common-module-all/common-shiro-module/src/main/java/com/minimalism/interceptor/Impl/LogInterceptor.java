package com.minimalism.interceptor.Impl;

import com.minimalism.abstractinterface.AbstractAuthorizationShiro;
import com.minimalism.abstractinterface.AuthorizationFilter;
import com.minimalism.constant.ExpressionConstants;
import com.minimalism.filter.JwtAuthFilter;
import com.minimalism.interceptor.AbstractLogInInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
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
public class LogInterceptor implements AbstractLogInInterceptor, AbstractAuthorizationShiro {
    @Override
    public void checkLogin(HttpServletRequest request, HttpServletResponse response) {
        AbstractAuthorizationShiro.super.checkTokenLogin(request, response);
    }
}

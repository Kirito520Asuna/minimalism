package com.minimalism.interceptor.Impl;

import com.minimalism.abstractinterface.AbstractAuthorizationSecurity;
import com.minimalism.constant.ExpressionConstants;
import com.minimalism.interceptor.AbstractLogInInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
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
@ConditionalOnExpression(ExpressionConstants.interceptorAllExpression)
public class LogInterceptor implements AbstractLogInInterceptor, AbstractAuthorizationSecurity {

    @Override
    public void checkLogin(HttpServletRequest request, HttpServletResponse response) {
        AbstractAuthorizationSecurity.super.checkTokenLogin(request, response);
        //String userIdNoThrow = SecurityContextUtil.getUserIdNoThrow();
        //if (ObjectUtil.isEmpty(userIdNoThrow)) {
        //    checkToken(request, response);
        //    SecurityContextUtil.getUserId();
        //}
    }

}

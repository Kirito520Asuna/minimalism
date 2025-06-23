package com.minimalism.auth.security.auth;

import com.minimalism.auth.security.abs.AbsAuthorizationSecurity;
import com.minimalism.basic.core.abs.auth.AbsAuthInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author yan
 * @Date 2025/6/13 19:58:19
 * @Description
 */
public class AuthSecurityInterceptor implements AbsAuthInterceptor, AbsAuthorizationSecurity {
    @Override
    public void checkLogin(HttpServletRequest request, HttpServletResponse response) {
        AbsAuthorizationSecurity.super.checkTokenLogin(request, response);
    }
}

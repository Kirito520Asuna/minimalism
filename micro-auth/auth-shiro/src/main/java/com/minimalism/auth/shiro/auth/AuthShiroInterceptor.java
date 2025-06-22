package com.minimalism.auth.shiro.auth;

import com.minimalism.auth.shiro.abs.AbsAuthorizationShiro;
import com.minimalism.basic.core.abs.auth.AbsAuthInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author yan
 * @Date 2025/6/14 00:27:48
 * @Description
 */
public class AuthShiroInterceptor implements AbsAuthInterceptor, AbsAuthorizationShiro {
    @Override
    public boolean checkToken(HttpServletRequest request, HttpServletResponse response) {
        return AbsAuthorizationShiro.super.checkToken(request, response);
    }
}

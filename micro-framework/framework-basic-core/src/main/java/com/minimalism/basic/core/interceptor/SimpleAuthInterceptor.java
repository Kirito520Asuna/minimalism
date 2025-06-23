package com.minimalism.basic.core.interceptor;

import com.minimalism.basic.core.abs.auth.AbsAuthInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author yan
 * @Date 2025/6/12 16:56:50
 * @Description
 */
public class SimpleAuthInterceptor implements AbsAuthInterceptor {
    @Override
    public void checkLogin(HttpServletRequest request, HttpServletResponse response) {
        log().warn("未实现授权登陆");
    }
}

package com.minimalism.interceptor;

import com.minimalism.abstractinterface.AbsInterceptor;
import lombok.SneakyThrows;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author yan
 * @Date 2024/10/27 下午10:26:58
 * @Description
 */
public interface AbsLogInInterceptor extends AbsInterceptor {
    @SneakyThrows
    @Override
    default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        checkLogin(request, response);
        return AbsInterceptor.super.preHandle(request, response, handler);
    }

    /**
     * 检查登陆
     */
    default void checkLogin(HttpServletRequest request, HttpServletResponse response) {
    }
}

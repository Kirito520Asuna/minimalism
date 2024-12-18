package com.minimalism.interceptor;

import cn.hutool.json.JSONUtil;
import com.minimalism.abstractinterface.AbstractInterceptor;
import com.minimalism.exception.GlobalCustomException;
import com.minimalism.result.Result;
import com.minimalism.utils.response.ResponseUtils;
import lombok.SneakyThrows;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author yan
 * @Date 2024/10/27 下午10:26:58
 * @Description
 */
public interface AbstractLogInInterceptor extends AbstractInterceptor {
    @SneakyThrows
    @Override
    default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        checkLogin(request, response);
        return AbstractInterceptor.super.preHandle(request, response, handler);
    }

    /**
     * 检查登陆
     */
    default void checkLogin(HttpServletRequest request, HttpServletResponse response) {
    }
}

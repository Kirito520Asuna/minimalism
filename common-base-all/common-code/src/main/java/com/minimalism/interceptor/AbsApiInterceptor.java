package com.minimalism.interceptor;

import com.minimalism.abstractinterface.AbsApiSign;
import com.minimalism.abstractinterface.AbsInterceptor;
import com.minimalism.pojo.http.CachedBodyHttpServletRequest;
import lombok.SneakyThrows;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author yan
 * @Date 2024/10/27 下午10:32:32
 * @Description
 */
public interface AbsApiInterceptor extends AbsInterceptor, AbsApiSign {
    @SneakyThrows
    @Override
    default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        CachedBodyHttpServletRequest cachedBodyHttpServletRequest = new CachedBodyHttpServletRequest(request);
        checkApi(request, cachedBodyHttpServletRequest);
        return AbsInterceptor.super.preHandle(request, response, handler);
    }
}

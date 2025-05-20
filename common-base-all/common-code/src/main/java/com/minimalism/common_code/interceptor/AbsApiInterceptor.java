package com.minimalism.common_code.interceptor;

import com.minimalism.common_code.abs.AbsApiSign;
import com.minimalism.common_code.abs.AbsInterceptor;
import com.minimalism.common_code.pojo.http.CachedBodyHttpServletRequest;
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

package com.minimalism.abstractinterface;

import cn.hutool.core.util.StrUtil;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.utils.thread.ThreadMdcUtil;
import lombok.SneakyThrows;
import org.slf4j.MDC;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author yan
 * @Date 2024/5/14 0014 14:45
 * @Description
 */
public interface AbstractInterceptor extends HandlerInterceptor, AbstractBean {
    String TRACE_ID = ThreadMdcUtil.TRACE_ID;

    @SneakyThrows
    @Override
    default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //可以考虑让客户端传入链路ID，但需保证一定的复杂度唯一性；如果没使用默认UUID自动生成
        String tid = StrUtil.isEmpty(request.getHeader(TRACE_ID)) ? ThreadMdcUtil.generateTraceId() : request.getHeader(TRACE_ID);
        MDC.put(TRACE_ID, tid);
        getLogger().info("preHandle by {}",getAClassName());
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    default void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
        MDC.remove(TRACE_ID);
        getLogger().info("afterCompletion by {}",getAClassName());
    }

    @Override
    default void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        getLogger().info("postHandle by {}", getAClassName());
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
}

package com.minimalism.basic.core.filter;

import com.minimalism.basic.core.abs.api.AbsApiFiler;
import com.minimalism.basic.core.pojo.http.CachedBodyHttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @Author yan
 * @Date 2025/6/12 16:33:45
 * @Description
 */
@Slf4j
public class ApiFilter extends OncePerRequestFilter implements AbsApiFiler {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        executeLog();
        // 从包装器读取请求体
        CachedBodyHttpServletRequest cachedBodyHttpServletRequest = new CachedBodyHttpServletRequest(request);
        checkApi(request, cachedBodyHttpServletRequest);
        //放行
        filterChain.doFilter(cachedBodyHttpServletRequest, response);
    }
}

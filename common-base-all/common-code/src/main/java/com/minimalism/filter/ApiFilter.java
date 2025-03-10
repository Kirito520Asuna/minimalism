package com.minimalism.filter;


import com.minimalism.abstractinterface.service.filter.AbstractApiFiler;
import com.minimalism.pojo.http.CachedBodyHttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @Author yan
 * @Date 2023/5/12 0012 18:09
 * @Description
 */
@Order(-2)
@Slf4j
public class ApiFilter extends OncePerRequestFilter implements AbstractApiFiler {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // 从包装器读取请求体
        CachedBodyHttpServletRequest cachedBodyHttpServletRequest = new CachedBodyHttpServletRequest(request);
        checkApi(request, cachedBodyHttpServletRequest);
        //放行
        filterChain.doFilter(cachedBodyHttpServletRequest, response);
    }

}
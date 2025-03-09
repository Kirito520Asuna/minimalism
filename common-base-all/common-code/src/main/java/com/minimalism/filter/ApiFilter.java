package com.minimalism.filter;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.service.AbstractApiFiler;
import com.minimalism.pojo.http.CachedBodyHttpServletRequest;
import com.minimalism.config.ApiConfig;
import com.minimalism.exception.GlobalCustomException;
import com.minimalism.abstractinterface.AbstractApiSign;
import com.minimalism.pojo.SaltInfo;
import com.minimalism.abstractinterface.service.AbstractApiSaltService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

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
package com.minimalism.filter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.abstractinterface.service.filter.AbstractFilerOrder;
import com.minimalism.exception.BusinessException;
import com.minimalism.utils.http.HttpRequestUtils;
import org.springframework.core.env.Environment;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Author yan
 * @Date 2025/5/6 09:05:19
 * @Description
 */
public class CommonFilter extends OncePerRequestFilter implements AbstractFilerOrder, AbstractBean {
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String requestSuffix = HttpRequestUtils.getRequestSuffix(httpServletRequest);
        //getLogger().debug("[Filter]-[CommonFilter]-[doFilterInternal]::[{}]: ",requestSuffix);
        //todo: 拦截
        getLogger().warn("未启用 通用拦截");
        //通行前缀
        List<String> passPrefix;
        Environment env = SpringUtil.getBean(Environment.class);
        String activeProfile = env.getProperty("spring.profiles.active","default");
        switch (activeProfile) {
            case "prod":
                passPrefix = CollUtil.newArrayList("/jwt", "/api");
                break;
            default:
                passPrefix = CollUtil.newArrayList("/");
                break;
        }

        boolean pass = false;
        for (String prefix : passPrefix) {
            boolean startsWith = requestSuffix.startsWith(prefix);
            if (startsWith) {
                pass = true;
                break;
            }
        }

        if (pass) {
            //放行
            getLogger().info("放行请求:{}", requestSuffix);
        }else {
            //拦截
            getLogger().warn("拦截请求:{}", requestSuffix);
            throw new BusinessException("拦截请求");
        }
        //放行
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}

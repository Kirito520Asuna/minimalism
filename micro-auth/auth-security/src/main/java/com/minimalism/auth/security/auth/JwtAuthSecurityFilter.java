package com.minimalism.auth.security.auth;

import com.minimalism.auth.security.abs.AuthSecurityFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @Author yan
 * @Date 2025/6/12 23:08:19
 * @Description
 */
public class JwtAuthSecurityFilter extends OncePerRequestFilter implements AuthSecurityFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        AuthSecurityFilter.super.doFilter(request, response, filterChain);
    }
}

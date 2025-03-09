package com.minimalism.filter;

import com.minimalism.abstractinterface.AbstractAuthorizationSecurity;
import com.minimalism.abstractinterface.AuthorizationFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @Author yan
 * @Date 2024/5/20 0020 17:02
 * @Description
 */
//@Component
//@Order(-1)
@Slf4j
public class JwtFilter extends OncePerRequestFilter implements AuthorizationFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        checkToken(request, response);
        //放行
        filterChain.doFilter(request, response);
    }
}

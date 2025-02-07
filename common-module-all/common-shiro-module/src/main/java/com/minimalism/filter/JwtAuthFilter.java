package com.minimalism.filter;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.google.gson.Gson;
import com.minimalism.abstractinterface.AbstractAuthorizationShiro;
import com.minimalism.config.JwtConfig;
import com.minimalism.utils.jwt.JwtUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author yan
 * @Date 2024/10/11 下午11:33:17
 * @Description
 */
public class JwtAuthFilter extends AuthenticatingFilter implements AbstractAuthorizationShiro {
    Gson gson = new Gson();

    //生成自定义token
    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        Logger log = getLogger();
        AuthenticationToken authenticationToken = null;
        try {

            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            // 从包装器读取请求体

            JwtUtils bean = SpringUtil.getBean(JwtUtils.class);
            JwtConfig jwtConfig = SpringUtil.getBean(JwtConfig.class);
            log.info("jwtConfig=>{}", jwtConfig);
            String tokenName = ObjectUtil.isEmpty(jwtConfig.getTokenName()) ? JwtUtils.HEADER_AS_TOKEN : jwtConfig.getTokenName();
            String userId = null;
            //获取token
            String token = request.getHeader(tokenName);
            if (StringUtils.hasText(token)) {
                //解析token
                String secret = bean.getSecret();
                boolean enableTwoToken = ObjectUtil.isNotEmpty(jwtConfig.getEnableTwoToken()) && jwtConfig.getEnableTwoToken();
                String refreshTokenName = ObjectUtil.isEmpty(jwtConfig.getRefreshTokenName()) ? JwtUtils.REFRESH_TOKEN_KEY : jwtConfig.getRefreshTokenName();
                userId = getUserIdByToken(enableTwoToken, secret, tokenName, refreshTokenName, request, response);
            }

            if (ObjectUtil.isNotEmpty(userId)) {
                UsernamePasswordToken usernamePasswordToken = generateUsernamePasswordToken(userId);
                authenticationToken = usernamePasswordToken;
            }

        } finally {
            return authenticationToken;
        }
    }
    // 判断是否允许访问

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        //getLogger().info("onAccessDenied");
        return executeLogin(request, response);
    }

    // 尝试登录
    @Override
    protected boolean executeLogin(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        return checkToken(request, response);
    }
}

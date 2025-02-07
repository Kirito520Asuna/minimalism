package com.minimalism.abstractinterface;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.service.AbstractAuthorization;
import com.minimalism.config.JwtConfig;
import com.minimalism.enums.ApiCode;
import com.minimalism.exception.GlobalCustomException;
import com.minimalism.pojo.UserInfo;
import com.minimalism.pojo.security.UserBase;
import com.minimalism.security.SecurityContextUtil;
import com.minimalism.service.AbstractUserDetailsService;
import com.minimalism.utils.jwt.JwtUtils;
import com.minimalism.utils.object.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Map;

/**
 * @Author yan
 * @Date 2024/10/14 上午2:06:15
 * @Description
 */
public interface AbstractAuthorizationSecurity extends AbstractAuthorization {
    @Override
    default String getTokenBySubstring(String token) {
        return AbstractAuthorization.super.getTokenBySubstring(token);
    }

    @Override
    default Map<String, String> pushTwoToken(String userId, String tokenName, String refreshTokenName, HttpServletResponse response) {
       return AbstractAuthorization.super.pushTwoToken(userId, tokenName, refreshTokenName, response);
    }

    @Override
    default String getUserIdByRefreshToken(String secret, String tokenName, String refreshTokenName, HttpServletRequest request, HttpServletResponse response) {
        return AbstractAuthorization.super.getUserIdByRefreshToken(secret, tokenName, refreshTokenName, request, response);
    }

    @Override
    default String getUserIdByToken(boolean enableTwoToken, String secret, String tokenName, String refreshTokenName, HttpServletRequest request, HttpServletResponse response) {
        return AbstractAuthorization.super.getUserIdByToken(enableTwoToken, secret, tokenName, refreshTokenName, request, response);
    }

    /**
     * 通过用户ID生成认证信息
     *
     * @param userId
     * @return
     */
    @NotNull
    default UsernamePasswordAuthenticationToken generateUsernamePasswordAuthenticationToken(String userId) {
        AbstractUserDetailsService userService = SpringUtil.getBean(AbstractUserDetailsService.class);
        UserBase user = userService.getLoginUser(userId);
        if (ObjectUtil.isEmpty(user)) {
            throw new GlobalCustomException(ApiCode.UNAUTHORIZED);
        }
        getLogger().info("用户信息：{}", user);
        //获取权限信息 使用数据库查询的信息
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        //String password = user.getPassword();
        //TODO 获取权限信息封装到Authentication中
        // 构建UsernamePasswordAuthenticationToken,这里密码为null，是因为提供了正确的JWT,实现自动登录
        UserInfo userInfo = user.getUser();
        if (ObjectUtil.isEmpty(userInfo)) {
            throw new GlobalCustomException(ApiCode.UNAUTHORIZED);
        }
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userInfo.getId(), null, authorities);
        return authenticationToken;
    }

    default boolean checkToken(HttpServletRequest request, HttpServletResponse response){
        Logger log = getLogger();
        JwtUtils bean = SpringUtil.getBean(JwtUtils.class);
        JwtConfig jwtConfig = SpringUtil.getBean(JwtConfig.class);
        log.info("jwtConfig=>{}", jwtConfig);
        String tokenName = ObjectUtils.defaultIfEmpty(jwtConfig.getTokenName(), JwtUtils.HEADER_AS_TOKEN);
        String userId = null;
        //获取token
        String token = request.getHeader(tokenName);
        if (StringUtils.hasText(token)) {
            //解析token
            String secret = bean.getSecret();
            boolean enableTwoToken = ObjectUtils.defaultIfEmpty(jwtConfig.getEnableTwoToken(), false);
            String refreshTokenName = ObjectUtils.defaultIfEmpty(jwtConfig.getRefreshTokenName(), JwtUtils.REFRESH_TOKEN_KEY);
            userId = getUserIdByToken(enableTwoToken, secret, tokenName, refreshTokenName, request, response);
        }

        if (ObjectUtil.isNotEmpty(userId)) {
            //存入SecurityContextHolder
            UsernamePasswordAuthenticationToken authenticationToken = generateUsernamePasswordAuthenticationToken(userId);
            SecurityContextUtil.getContext().setAuthentication(authenticationToken);
            log.info("Authentication=>{},userId=>{},anyRoles=>{};"
                    , SecurityContextUtil.getAuthentication()
                    , SecurityContextUtil.getUserId()
                    , SecurityContextUtil.getAnyRoles()
            );
        }
        return true;
    }


    default void checkTokenLogin(HttpServletRequest request, HttpServletResponse response) {
        String userIdNoThrow = SecurityContextUtil.getUserIdNoThrow();
        if (ObjectUtil.isEmpty(userIdNoThrow)) {
            checkToken(request, response);
            SecurityContextUtil.getUserId();
        }
    }
}

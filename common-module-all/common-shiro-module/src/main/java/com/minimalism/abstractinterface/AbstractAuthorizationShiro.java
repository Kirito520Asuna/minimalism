package com.minimalism.abstractinterface;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.minimalism.abstractinterface.service.AbstractAuthorization;
import com.minimalism.config.JwtConfig;
import com.minimalism.enums.ApiCode;
import com.minimalism.exception.GlobalCustomException;
import com.minimalism.pojo.shiro.UserBase;
import com.minimalism.result.Result;
import com.minimalism.utils.jwt.JwtUtils;
import com.minimalism.utils.object.ObjectUtils;
import com.minimalism.utils.response.ResponseUtils;
import com.minimalism.utils.shiro.SecurityContextUtil;
import lombok.SneakyThrows;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Author yan
 * @Date 2024/10/20 上午5:01:59
 * @Description
 */
public interface AbstractAuthorizationShiro extends AbstractAuthorization {

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
     * 获取用户信息
     *
     * @param userId
     * @return
     */
    @NotNull
    default UsernamePasswordToken generateUsernamePasswordToken(String userId) {
        AbstractUserDetailsByShiroService userService = SpringUtil.getBean(AbstractUserDetailsByShiroService.class);
        UserBase user = userService.getLoginUser(userId);
        if (ObjectUtil.isEmpty(user)) {
            throw new GlobalCustomException(ApiCode.UNAUTHORIZED);
        }
        info("用户信息：{}", user);
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(user.getUsername(), user.getPassword());
        return usernamePasswordToken;
    }

    @SneakyThrows
    default boolean checkToken(HttpServletRequest request, HttpServletResponse response) {

        JwtUtils bean = SpringUtil.getBean(JwtUtils.class);
        JwtConfig jwtConfig = SpringUtil.getBean(JwtConfig.class);
        info("jwtConfig=>{}", jwtConfig);
        String tokenName = ObjectUtils.defaultIfEmpty(jwtConfig.getTokenName(), JwtUtils.HEADER_AS_TOKEN);
        String userId = null;
        //获取token
        String token = request.getHeader(tokenName);
        boolean executeLogin = false;
        try {
            if (StringUtils.hasText(token)) {
                //解析token
                String secret = bean.getSecret();
                boolean enableTwoToken = ObjectUtils.defaultIfEmpty(jwtConfig.getEnableTwoToken(), false);
                String refreshTokenName = ObjectUtil.defaultIfEmpty(jwtConfig.getRefreshTokenName(), JwtUtils.REFRESH_TOKEN_KEY);
                userId = getUserIdByToken(enableTwoToken, secret, tokenName, refreshTokenName, request, response);
            }

            if (ObjectUtil.isNotEmpty(userId)) {
                //存入SecurityContextHolder
                UsernamePasswordToken usernamePasswordToken = generateUsernamePasswordToken(userId);
                SecurityContextUtil.login(userId, usernamePasswordToken);
                info("Subject:{},Principal:{},UserId:{}"
                        , SecurityContextUtil.getSubject()
                        , SecurityContextUtil.getPrincipal()
                        , SecurityContextUtil.getUserId()
                );
                executeLogin = true;
            } else {
                boolean isOpenFilter = ObjectUtils.defaultIfEmpty(jwtConfig.getOpenFilter(), true);
                if (isOpenFilter) {
                    String contentType = "application/json;charset=UTF-8";
                    Result result = Result.result(ApiCode.UNAUTHORIZED);
                    String json = JSONUtil.toJsonStr(result, JSONConfig.create().setIgnoreNullValue(false));
                    ResponseUtils.responsePush(response, contentType, json);
                    error("未授权");
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return executeLogin;
    }

    default void checkTokenLogin(HttpServletRequest request, HttpServletResponse response) {
        String userIdNoThrow = SecurityContextUtil.getUserIdNoThrow();
        if (ObjectUtil.isEmpty(userIdNoThrow)) {
            checkToken(request, response);
            SecurityContextUtil.getUserId();
        }
    }
}

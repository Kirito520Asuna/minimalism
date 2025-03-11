package com.minimalism.abstractinterface.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.google.common.collect.Maps;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.config.JwtConfig;
import com.minimalism.enums.ApiCode;
import com.minimalism.exception.GlobalCustomException;
import com.minimalism.utils.date.DateUtils;
import com.minimalism.utils.jwt.JwtUtils;
import com.minimalism.utils.object.ObjectUtils;
import com.minimalism.utils.response.ResponseUtils;
import io.jsonwebtoken.Claims;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

/**
 * @Author yan
 * @Date 2024/10/20 上午4:54:32
 * @Description
 */
public interface AbstractAuthorization extends AbstractBean {
    @Override
    default void init() {
        debug("[Bean]-[Authorization]-[init]::[{}]: ",getAClassName());
    }

    /**
     * 获取token
     *
     * @param token
     * @return
     */
    default String getTokenBySubstring(String token) {
        String prefix = "Bearer ";
        if (token.startsWith(prefix)) {
            token = new StringBuffer(token).substring(prefix.length() - 1).toString();
        }
        return token;
    }

    /**
     * 重新生成双token 推送至响应器
     * @param userId
     * @param tokenName
     * @param refreshTokenName
     * @param response
     */
    default Map<String, String>  pushTwoToken(String userId, String tokenName, String refreshTokenName, HttpServletResponse response) {
        String token = JwtUtils.createJWT(userId);
        Map<String, String> hashMap = Maps.newLinkedHashMap();
        hashMap.put(tokenName, token);
        String msg = "one-";
        if (StrUtil.isNotBlank(refreshTokenName)){
            String refreshToken = JwtUtils.createJWT(userId, JwtUtils.getLONG_JWT_TTL());
            hashMap.put(refreshTokenName, refreshToken);
            msg = "two-";
        }
        ResponseUtils.responseSetHeader(response,hashMap);
        getLogger().info("[{}]生成{}token推送至响应器",getAClass().getName(),msg);
        return hashMap;
    }

    /**
     * 获取用户ID 通过refreshToken 并重新生成双token 推送至响应器
     * @param secret 秘钥
     * @param tokenName token名称
     * @param refreshTokenName refreshToken名称
     * @param request
     * @param response
     * @return
     */
    default String getUserIdByRefreshToken(String secret, String tokenName, String refreshTokenName, HttpServletRequest request, HttpServletResponse response) {
        refreshTokenName = ObjectUtils.defaultIfEmpty(refreshTokenName, JwtUtils.HEADER_AS_TOKEN);
        //获取token
        String refreshToken = request.getHeader(refreshTokenName);
        refreshToken = getTokenBySubstring(refreshToken);
        String userId = null;
        try {
            Claims claims = JwtUtils.parseJWT(refreshToken, secret);
            if (ObjectUtil.isEmpty(claims)) {
                throw new GlobalCustomException(ApiCode.UNAUTHORIZED);
            }

            boolean tokenExpired = JwtUtils.isTokenExpired(claims);

            if (tokenExpired) {
                throw new GlobalCustomException(ApiCode.UNAUTHORIZED);
            }
            userId = claims.getSubject();
            pushTwoToken(userId, tokenName, refreshTokenName, response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalCustomException(ApiCode.UNAUTHORIZED);
        }
        getLogger().info("[{}]刷新长token处理",getAClass().getName());
        return userId;
    }

    /**
     * 获取用户ID 通过Token
     * @param openTwoToken 是否开启双token？
     * @param secret 秘钥
     * @param tokenName token名称
     * @param refreshTokenName refreshToken名称
     * @param request
     * @param response
     * @return
     */
    default String getUserIdByToken(boolean openTwoToken, String secret, String tokenName, String refreshTokenName, HttpServletRequest request, HttpServletResponse response) {
        tokenName = ObjectUtils.defaultIfEmpty(tokenName,JwtUtils.HEADER_AS_TOKEN);
        //获取token
        String token = request.getHeader(tokenName);
        token = getTokenBySubstring(token);
        String userId = null;
        try {
            Claims claims = JwtUtils.parseJWT(token, secret);
            if (ObjectUtil.isEmpty(claims)) {
                throw new GlobalCustomException(ApiCode.UNAUTHORIZED);
            }

            boolean tokenExpired = JwtUtils.isTokenExpired(claims);

            if (tokenExpired) {
                if (openTwoToken) {
                    userId = getUserIdByRefreshToken(secret, tokenName, refreshTokenName, request, response);
                } else {
                    throw new GlobalCustomException(ApiCode.UNAUTHORIZED);
                }
            } else {
                userId = claims.getSubject();
                String msg = "two-token模式";
                if (!openTwoToken){
                    msg = "one-token模式";
                    Long refreshOneTokenLong = ObjectUtils.defaultIfEmpty(SpringUtil.getBean(JwtConfig.class).getRefreshOneTokenLong(),5l);
                    Date date = DateUtils.LocalDateTimeToDate(LocalDateTime.now().plusMinutes(refreshOneTokenLong));
                    boolean tokenExpiredPass = JwtUtils.isTokenExpired(claims, date);
                    if (tokenExpiredPass){
                        pushTwoToken(userId, tokenName, null, response);
                    }
                }
                getLogger().info("[{}][{}]获取用户ID通过Token",getAClass().getName(),msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalCustomException(ApiCode.UNAUTHORIZED);
        }
        return userId;
    }

}

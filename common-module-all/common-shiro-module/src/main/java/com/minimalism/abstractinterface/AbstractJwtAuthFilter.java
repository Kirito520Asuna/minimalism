package com.minimalism.abstractinterface;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.enums.ApiCode;
import com.minimalism.exception.GlobalCustomException;
import com.minimalism.pojo.shiro.UserBase;
import com.minimalism.utils.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author yan
 * @Date 2024/10/12 上午5:22:31
 * @Description
 */
@Deprecated
public interface AbstractJwtAuthFilter extends AbstractBean {
    /**
     * 获取用户信息
     *
     * @param userId
     * @return
     */
    @NotNull @Deprecated
    default UsernamePasswordToken getUsernamePasswordToken(String userId) {
        Logger log = getLogger();
        AbstractUserDetailsByShiroService userService = SpringUtil.getBean(AbstractUserDetailsByShiroService.class);
        UserBase user = userService.getLoginUser(userId);
        if (ObjectUtil.isEmpty(user)) {
            throw new GlobalCustomException(ApiCode.UNAUTHORIZED);
        }
        log.info("用户信息：{}", user);
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(user.getUsername(), user.getPassword());
        return usernamePasswordToken;
    }

    /**
     * 获取用户id
     *
     * @param request
     * @return
     */
    @Deprecated
    default String getUserId(HttpServletRequest request) {
        String userId = null;
        try {
            JwtUtils bean = SpringUtil.getBean(JwtUtils.class);

            HttpServletRequest httpRequest = request;
            String tokenAsHeader = ObjectUtil.isEmpty(bean.getHeader()) ? JwtUtils.HEADER_AS_TOKEN : bean.getHeader();

            //获取token
            String token = httpRequest.getHeader(tokenAsHeader);
            if (StringUtils.hasText(token)) {
                String prefix = "Bearer ";
                if (token.startsWith(prefix)) {
                    token = new StringBuffer(token).substring(prefix.length() - 1).toString();
                }
                //解析token
                try {
                    Claims claims = JwtUtils.parseJWT(token, bean.getSecret());
                    if (ObjectUtil.isEmpty(claims)) {
                        throw new GlobalCustomException(ApiCode.UNAUTHORIZED);
                    }

                    boolean tokenExpired = JwtUtils.isTokenExpired(claims);

                    if (tokenExpired) {
                        throw new GlobalCustomException(ApiCode.UNAUTHORIZED);
                    }
                    userId = claims.getSubject();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new GlobalCustomException(ApiCode.UNAUTHORIZED);
                }
            }
        } finally {
            return userId;
        }
    }
}

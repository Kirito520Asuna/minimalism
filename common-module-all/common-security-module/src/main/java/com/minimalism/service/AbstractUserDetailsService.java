package com.minimalism.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.service.AbstractUserService;
import com.minimalism.aop.redis.RedisCacheEvict;
import com.minimalism.aop.redis.RedisCachePut;
import com.minimalism.aop.redis.RedisCacheable;
import com.minimalism.constant.Redis;
import com.minimalism.enums.ApiCode;
import com.minimalism.exception.GlobalCustomException;
import com.minimalism.pojo.security.UserBase;
import com.minimalism.pojo.UserInfo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Objects;

/**
 * @Author yan
 * @Date 2024/5/20 0020 17:10
 * @Description
 */
public interface AbstractUserDetailsService extends UserDetailsService  {
    default RedisTemplate getRedisTemplate() {
        return SpringUtil.getBean(RedisTemplate.class);
    }

    @Override
    default UserBase loadUserByUsername(String username) throws UsernameNotFoundException {
        AbstractUserService userService = SpringUtil.getBean(AbstractUserService.class);
        UserInfo userInfo = userService.getOneByUserName(username);

        if (userInfo == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        //roles权限 --可扩展权限系统
        List<String> roles = userService.getRolesById(Long.parseLong(userInfo.getId()));
        if (CollUtil.isEmpty(roles)) {
            roles = null;
        }
        UserBase user = new UserBase(userInfo, roles);
        return user;
    }

    /**
     * 获取登录用户获取 用户信息 需重写
     *
     * @param userId
     * @return
     */
    //@Cacheable(key = "'" + Redis.login_user + ":' + #userId")
    @RedisCacheable(cacheName = Redis.login_user,requestAsName = "rq",
            condition = "#rq.userId!=null",throwException = false,
            key = "#rq.userId",classType = UserBase.class)
    default UserBase getLoginUser(String userId) {
        //UserBase user = (UserBase) getRedisTemplate().opsForValue().get(Redis.login_user + ":" + userId);
        return null;
    }

    /**
     * 登录 可重写
     *
     * @param username
     * @param password
     * @return
     */
    @RedisCachePut(cacheName = Redis.login_user, condition = "#re!=null&&#re.user!=null", key = "#re.user.id", responseAsName = "re")
    default UserBase loginByUsernamePassword(String username, String password) {
        AuthenticationManager authenticationManager = SpringUtil.getBean(AuthenticationManager.class);
        //AuthenticationManager authenticationManager 进行用户认证
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticate = null;
        try {
            authenticate = authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //认证没通过
        if (Objects.isNull(authenticate)) {
            throw new GlobalCustomException(ApiCode.USERNAME_PASSWORD_EORR);
        }
        UserBase user = (UserBase) authenticate.getPrincipal();
        UserBase userMap = loadUserByUsername(username);
        BeanUtil.copyProperties(userMap, user, "password", "user.password");
        return user;
    }

    /**
     * 退出登录 可重写
     *
     * @param id
     */
    //@CacheEvict(key = "'" + Redis.login_user + ":' + #id")
    @RedisCacheEvict(cacheName = Redis.login_user,requestAsName = "rq",condition = "#rq.id!=null", key = "#rq.id")
    default void logout(String id) {
        //RedisTemplate bean = SpringUtil.getBean(RedisTemplate.class);
        //getRedisTemplate().delete(Redis.login_user + ":" + id);
    }
}

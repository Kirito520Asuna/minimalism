package com.minimalism.utils.shiro;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.minimalism.abstractinterface.AbstractUserDetailsByShiroService;
import com.minimalism.constant.Redis;
import com.minimalism.constant.Roles;
import com.minimalism.enums.ApiCode;
import com.minimalism.exception.GlobalCustomException;
import com.minimalism.pojo.shiro.UserBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * @Author yan
 * @Date 2024/10/12 上午2:27:41
 * @Description
 */
@Slf4j
public class SecurityContextUtil{
    public static ThreadLocal<UserHolder> threadLocal = new TransmittableThreadLocal<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    static class UserHolder {
        private String userId;
        private List<String> roles = CollUtil.newArrayList();
    }

    public static Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    public static Object getPrincipal() {
        return getSubject().getPrincipal();
    }

    public static String getUsername() {
        return (String) getPrincipal();
    }

    public static void setUserId(String userId) {
        UserHolder userHolder = threadLocal.get();
        if (ObjectUtil.isEmpty(userHolder)) {
            userHolder = new UserHolder();
        }
        userHolder.setUserId(userId);
        if (StrUtil.isNotBlank(userId)) {
            AbstractUserDetailsByShiroService bean = SpringUtil.getBean(AbstractUserDetailsByShiroService.class);
            UserBase user = bean.getLoginUser(userId);
            if (ObjectUtil.isNotEmpty(user)) {
                userHolder.setRoles(user.getRoles());
            }
        }
        threadLocal.set(userHolder);
        log.info("setUserId: " + userId);

    }

    public static String getUserId() {
        String userId = getUserIdNoThrow();
        if (StrUtil.isBlank(userId)) {
            throw new GlobalCustomException(ApiCode.UNAUTHORIZED);
        }
        return userId;
    }

    public static String getUserIdNoThrow() {
        UserHolder userHolder = ObjectUtil.defaultIfNull(threadLocal.get(), new UserHolder());
        String userId = userHolder.getUserId();
        if (StrUtil.isBlank(userId)) {
            String username = getUsername();
            if (StrUtil.isNotBlank(username)) {
                RedisTemplate redisTemplate = SpringUtil.getBean(RedisTemplate.class);
                userId = (String) redisTemplate.opsForHash().get(Redis.login_username_userId_map, username);
            }
        }
        return userId;
    }

    public static void login(String userId, String username, String password) throws Exception {
        boolean throwException = false;
        try {
            // shiro登录认证
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            login(token);
        } catch (Exception e) {
            throwException = true;
            throw e;
        } finally {
            if (!throwException) {
                RedisTemplate redisTemplate = SpringUtil.getBean(RedisTemplate.class);
                redisTemplate.opsForHash().put(Redis.login_username_userId_map, username, userId);
                setUserId(userId);
            }
        }

    }

    public static void login(String userId, UsernamePasswordToken usernamePasswordToken) {
        boolean throwException = false;
        try {
            // shiro登录认证
            login(usernamePasswordToken);
        } catch (Exception e) {
            throwException = true;
            throw e;
        } finally {
            if (!throwException) {
                RedisTemplate redisTemplate = SpringUtil.getBean(RedisTemplate.class);
                redisTemplate.opsForHash().put(Redis.login_username_userId_map, usernamePasswordToken.getUsername(), userId);
                setUserId(userId);
            }
        }
    }

    public static void login(AuthenticationToken authenticationToken) {
        getSubject().login(authenticationToken);
    }

    public static void logout() {
        getSubject().logout();
        threadLocal.remove();
    }

    /**
     * 获取当前用户角色
     *
     * @return
     */
    public static List<String> getRoles() {
        List<String> roles = CollUtil.newArrayList();
        UserHolder userHolder = threadLocal.get();
        if (ObjectUtil.isNotEmpty(userHolder)) {
            userHolder.getRoles()
                    .stream()
                    .filter(role -> role.startsWith(Roles.roles))
                    .forEach(role -> roles.add(role));
        }
        return roles;
    }

    /**
     * 获取当前用户权限
     *
     * @return
     */
    public static List<String> getPermissions() {
        List<String> permissions = CollUtil.newArrayList();
        UserHolder userHolder = threadLocal.get();
        if (ObjectUtil.isNotEmpty(userHolder)) {
            userHolder.getRoles()
                    .stream()
                    .filter(role -> role.startsWith(Roles.perms))
                    .forEach(role -> permissions.add(role));
        }
        return permissions;
    }
}

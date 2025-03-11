package com.minimalism.abstractinterface.security;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.config.AuthorizationConfig;
import com.minimalism.constant.Roles;
import com.minimalism.utils.object.ObjectUtils;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @Author yan
 * @Date 2024/9/27 上午2:12:25
 * @Description
 */
public interface AbstractSecurityExpressionRoot extends AbstractBean {
    @Override
    @PostConstruct
    default void init() {
        debug("[init]-[SecurityExpressionRoot]::[{}]", getAClassName());
    }

    /**
     * @param key
     * @return
     */
    default boolean isAdmin(String key) {
        String property = SpringUtil.getBean(AuthorizationConfig.class).getAdminKey();
        String admin = ObjectUtils.defaultIfEmpty(property, "admin");
        key = ObjectUtils.defaultIfEmpty(key, "");

        if (key.startsWith(Roles.roles)) {
            key = key.replace(Roles.roles, "");
        } else if (key.startsWith(Roles.perms)) {
            key = key.replace(Roles.perms, "");
        }

        if (admin.startsWith(Roles.roles)) {
            admin = admin.replace(Roles.roles, "");
        } else if (admin.startsWith(Roles.perms)) {
            admin = admin.replace(Roles.perms, "");
        }

        return ObjectUtils.equals(admin, key);
    }

    default boolean isAdmin(List<String> keys) {
        keys = ObjectUtils.defaultIfEmpty(keys, new ArrayList<>());
        boolean isAdmin = false;

        for (String key : keys) {
            isAdmin = isAdmin || isAdmin(key);
            if (isAdmin) {
                break;
            }
        }
        return isAdmin;
    }

    /**
     * 自定义认证
     *
     * @param authority
     * @return
     */
    default boolean hasAuthority(String authority) {
        boolean hasAuthority = false;
        try {
            hasAuthority = isAdmin(getRoles()) || getAuthorityList().contains(authority);
        } catch (Exception e) {
            error("err {} ", e.getMessage());
        }
        return hasAuthority;
    }

    default boolean hasRole(String role) {
        boolean hasRole = false;

        if (!role.startsWith(Roles.roles)) {
            role = new StringBuffer(Roles.roles).append(role).toString();
        }

        try {
            hasRole = isAdmin(role) || getRoles().contains(role);
        } catch (Exception e) {
            error("err {} ", e.getMessage());
        }
        return hasRole;
    }

    default List<String> getAuthorityList() {
        return getAnyRoles().stream()
                .filter(roleOne -> roleOne.startsWith(Roles.perms))
                .map(o -> o.replace(Roles.perms, ""))
                .collect(Collectors.toList());
    }

    default List<String> getRoles() {
        return getAnyRoles().stream()
                .filter(roleOne -> roleOne.startsWith(Roles.roles))
                .map(o -> o.replace(Roles.roles, ""))
                .collect(Collectors.toList());
    }

    default List<String> getAnyRoles() {
        return CollUtil.newArrayList();
    }
}

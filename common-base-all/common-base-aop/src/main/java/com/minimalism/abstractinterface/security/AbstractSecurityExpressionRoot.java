package com.minimalism.abstractinterface.security;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.constant.Roles;
import com.minimalism.utils.object.ObjectUtils;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.stream.Collectors;


/**
 * @Author yan
 * @Date 2024/9/27 上午2:12:25
 * @Description
 */
public interface AbstractSecurityExpressionRoot extends AbstractBean {
    /**
     * @param key
     * @return
     */
    default boolean isAdmin(String key) {
        Environment env = SpringUtil.getBean(Environment.class);
        String property = env.getProperty("auth.admin");
        String admin = ObjectUtils.defaultIfEmpty(property, "admin");
        key = ObjectUtils.defaultIfEmpty(key, "");

        if (key.startsWith(Roles.roles)) {
            key = key.replace(Roles.roles, "");
        } else if (key.startsWith(Roles.perms)) {
            key = key.replace(Roles.perms, "");
        }

        if (admin.startsWith(Roles.roles)) {
            admin = admin.replace(Roles.roles, "");
        }else if (admin.startsWith(Roles.perms)) {
            admin = admin.replace(Roles.perms, "");
        }

        return ObjectUtils.equals(admin, key);
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
            //List<String> roles = getAuthorityList();
            hasAuthority = isAdmin(authority) || getAuthorityList().contains(authority);
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
            //List<String> roles = getRoles();
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

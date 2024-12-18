package com.minimalism.abstractinterface.security;

import cn.hutool.core.collection.CollUtil;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.constant.Roles;

import java.util.List;
import java.util.stream.Collectors;


/**
 * @Author yan
 * @Date 2024/9/27 上午2:12:25
 * @Description
 */
public interface AbstractSecurityExpressionRoot extends AbstractBean {
    /**
     * 自定义认证
     * @param authority
     * @return
     */
    default boolean hasAuthority(String authority) {
        boolean hasAuthority = false;
        try {
            List<String> roles = getAuthorityList();
            hasAuthority = roles.contains(authority);
        } catch (Exception e) {
            getLogBean().getLogger().error("err {} ", e.getMessage());
        }
        return hasAuthority;
    }

    default boolean hasRole(String role) {
        boolean hasRole = false;
        try {
            List<String> roles = getRoles();
            if (!role.startsWith(Roles.roles)) {
                role = new StringBuffer(Roles.roles).append(role).toString();
            }
            hasRole = roles.contains(role);
        } catch (Exception e) {
            getLogBean().getLogger().error("err {} ", e.getMessage());
        }
        return hasRole;
    }

    default List<String> getAuthorityList() {
        return getAnyRoles().stream().filter(roleOne -> roleOne.startsWith(Roles.perms)).collect(Collectors.toList());
    }

    default List<String> getRoles() {
        return getAnyRoles().stream().filter(roleOne -> roleOne.startsWith(Roles.roles)).collect(Collectors.toList());
    }

    default List<String> getAnyRoles() {
        return CollUtil.newArrayList();
    }
}

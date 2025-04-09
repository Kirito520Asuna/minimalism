package com.minimalism.security;


import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.security.AbstractSecurityExpressionRoot;
import com.minimalism.config.security.SecurityAnnotationConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author yan
 * @Date 2024/9/26 下午10:13:08
 * @Description
 */
@Component("custom")
@Slf4j
public class CustomSecurityExpressionRoot implements AbstractSecurityExpressionRoot {
    //@PreAuthorize("custom.hasAuthority('admin')")
    //@PreAuthorize("custom.hasRole('admin')")
    public boolean enable() {
        boolean enable = SpringUtil.getBean(SecurityAnnotationConfig.class).isEnable();
        return enable;
    }

    /**
     * 自定义认证
     *
     * @param authority
     * @return
     */
    @Override
    public boolean hasAuthority(String authority) {
        if (!enable()) {
            log.debug("未开启权限认证");
            return true;
        }
        return AbstractSecurityExpressionRoot.super.hasAuthority(authority);
    }

    @Override
    public boolean hasRole(String role) {
        if (!enable()) {
            log.debug("未开启权限认证");
            return true;
        }
        return AbstractSecurityExpressionRoot.super.hasRole(role);
    }

    @Override
    public List<String> getAnyRoles() {
        return SecurityContextUtil.getAnyRoles();
    }
}

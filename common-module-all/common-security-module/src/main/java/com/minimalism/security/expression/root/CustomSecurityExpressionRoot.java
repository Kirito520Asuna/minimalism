package com.minimalism.security.expression.root;


import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.security.AbsSecurityExpressionRoot;
import com.minimalism.security.config.SecurityAnnotationConfig;
import com.minimalism.security.utils.SecurityContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author yan
 * @Date 2024/9/26 下午10:13:08
 * @Description
 */
@Component("custom")
@Slf4j
public class CustomSecurityExpressionRoot  implements AbsSecurityExpressionRoot {

    //@PreAuthorize("@custom.hasAuthority('admin')")
    //@PreAuthorize("@custom.hasRole('admin')")
    public boolean enable() {
        boolean enable = SpringUtil.getBean(SecurityAnnotationConfig.class).isEnable();
        return enable;
    }
    //org.springframework.security.access.expression.method.MethodSecurityExpressionRoot
    /**
     * 自定义认证
     *
     * @param authority
     * @return
     */
    @Override
    public boolean hasAuthority(String authority) {
        if (!enable()) {
            log.warn("未开启权限认证");
            return true;
        }
        return AbsSecurityExpressionRoot.super.hasAuthority(authority);
    }

    @Override
    public boolean hasRole(String role) {
        if (!enable()) {
            log.warn("未开启权限认证");
            return true;
        }
        return AbsSecurityExpressionRoot.super.hasRole(role);
    }

    @Override
    public List<String> getAnyRoles() {
        return SecurityContextUtil.getAnyRoles();
    }
}

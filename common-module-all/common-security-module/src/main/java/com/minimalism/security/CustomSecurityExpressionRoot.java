package com.minimalism.security;


import com.minimalism.abstractinterface.security.AbstractSecurityExpressionRoot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author yan
 * @Date 2024/9/26 下午10:13:08
 * @Description
 */
@Component("custom") @Slf4j
public class CustomSecurityExpressionRoot implements AbstractSecurityExpressionRoot {
    /**
     * 自定义认证
     * @param authority
     * @return
     */
    @Override
    public boolean hasAuthority(String authority){
        return AbstractSecurityExpressionRoot.super.hasAuthority(authority);
    }
    @Override
    public boolean hasRole(String role){
        return AbstractSecurityExpressionRoot.super.hasRole(role);
    }
    @Override
    public List<String> getAnyRoles(){
        return SecurityContextUtil.getAnyRoles();
    }
}

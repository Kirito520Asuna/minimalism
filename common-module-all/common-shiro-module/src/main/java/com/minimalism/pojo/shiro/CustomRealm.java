package com.minimalism.pojo.shiro;

import com.minimalism.abstractinterface.AbstractShiroAuthorization;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;


/**
 * @Author yan
 * @Date 2024/10/11 下午9:55:10
 * @Description
 */
public class CustomRealm extends AuthorizingRealm implements AbstractShiroAuthorization {
    @Override
    public AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return AbstractShiroAuthorization.super.abstractDoGetAuthorizationInfo(principalCollection);
    }

    @Override
    public AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        return AbstractShiroAuthorization.super.abstractDoGetAuthenticationInfo(authenticationToken);
    }

    @Override
    public String getRealmName() {
        return getName();
    }
}

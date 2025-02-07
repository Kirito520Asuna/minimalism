package com.minimalism.abstractinterface;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.abstractinterface.service.AbstractUserService;
import com.minimalism.constant.Roles;
import com.minimalism.exception.GlobalCustomException;
import com.minimalism.pojo.UserInfo;
import com.minimalism.utils.shiro.SecurityContextUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;

import java.util.List;

/**
 * @Author yan
 * @Date 2024/10/11 下午10:44:35
 * @Description
 */
public interface AbstractShiroAuthorization extends AbstractBean {
    /**
     * 授权
     */
    default AuthorizationInfo abstractDoGetAuthorizationInfo(PrincipalCollection principalCollection) {
        Logger log = getLogger();
        log.info("开始授权");
        String username = SecurityContextUtil.getUsername();
        //获取UserService对象
        AbstractUserService userService = SpringUtil.getBean(AbstractUserService.class);
        //基于角色授权
        UserInfo userInfo = userService.getOneByUserName(username);
        List<String> roles = userService.getRolesById(Long.parseLong(userInfo.getId()));
        userInfo.setRoles(roles);
        SimpleAuthorizationInfo authorizationInfo = null;
        if (CollUtil.isNotEmpty(userInfo.getRoles())) {
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            userInfo.getRoles().forEach(role -> {
                if (role.startsWith(Roles.roles)) {
                    info.addRole(role);
                } else if (role.startsWith(Roles.perms)) {
                    info.addStringPermission(role);
                }
            });
            authorizationInfo = info;
        }
        return authorizationInfo;
    }

    /**
     * 认证
     */

    default AuthenticationInfo abstractDoGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // 获取用户输入的用户名和密码
        UsernamePasswordToken userToken = (UsernamePasswordToken) authenticationToken;
        String username = userToken.getUsername();
        String password = new String(userToken.getPassword());
        AbstractUserService userService = SpringUtil.getBean(AbstractUserService.class);
        UserInfo userInfo = userService.getOneByUserName(username);
        if (ObjectUtil.isEmpty(userInfo)) {
            throw new GlobalCustomException("用户不存在");
        }
        // 用户数据
        String correctPassword = userInfo.getPassword();

        boolean matchPassword = ObjectUtil.equal(password, correctPassword);
        //boolean enableEncodePassword = ShiroConfig.getEnableEncodePassword();
        //if (enableEncodePassword){
        //    matchPassword = EncodePasswordUtils.matchPassword(password, correctPassword);
        //}
        // 判断密码是否正确
        if (!matchPassword) {
            throw new GlobalCustomException("密码错误");
        }
        // 认证成功，返回身份信息
        return new SimpleAuthenticationInfo(username, correctPassword, getRealmName());
    }

    default void login(String username, String password) throws Exception {
        // shiro登录认证
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        SecurityContextUtil.login(token);
    }

    /**
     * @return
     */
    String getRealmName();
}

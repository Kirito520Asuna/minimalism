package com.minimalism.abstractinterface.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.config.JwtConfig;
import com.minimalism.constant.Redis;
import com.minimalism.constant.Roles;
import com.minimalism.pojo.TokenInfo;
import com.minimalism.pojo.User;
import com.minimalism.pojo.UserInfo;
import com.minimalism.utils.jwt.JwtUtils;
import com.minimalism.utils.object.ObjectUtils;

import java.util.List;
import java.util.logging.Logger;

/**
 * @Author yan
 * @Date 2024/5/20 0020 17:39
 * @Description
 */
public interface AbstractUserService {


    //Logger log = Logger.getLogger(AbstractUserService.class.getName());
    default UserInfo getOneByUserName(String userName) {
        return new UserInfo()
                .setId("-1")
                .setUsername(userName)
                //原密码123456 加密形成
                .setPassword("$2a$10$fLuXlTai3PEgUNMySVRN7eR9JAZcsrQmylFT6jdoRJLbjTSj2o1FO")
                .setRoles(CollUtil.newArrayList("admin"))
                .setImage("")
                .setNickname("admin")
                .setAccountStatus(true);
    }

    /**
     * 登录
     *
     * @param user
     * @return
     */
    default TokenInfo login(UserInfo user) {
        User userRe = SpringUtil.getBean(AbstractUserService.class).login(user.getUsername(), user.getPassword());
        String id = userRe.getUser().getId();
        JwtConfig jwtConfig = SpringUtil.getBean(JwtConfig.class);
        String tokenName = jwtConfig.getTokenName();
        String refreshTokenName = jwtConfig.getRefreshTokenName();
        Boolean openTwoToken = ObjectUtils.defaultIfEmpty(jwtConfig.getEnableTwoToken(), false);
        String token = JwtUtils.createJWT(id);
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setTokenName(tokenName)
                .setOpenTwoToke(openTwoToken)
                .setTokenValue(token);
        if (openTwoToken) {
            String refreshToken = JwtUtils.createJWT(id, JwtUtils.getLONG_JWT_TTL());
            tokenInfo.setRefreshTokenName(refreshTokenName)
                    .setRefreshTokenValue(refreshToken);
        }
        return tokenInfo;
    }

    //@RedisCachePut(cacheName = Redis.login_user, condition = "#re!=null&&#re.user!=null", key = "#re.user.id", responseAsName = "re")
    default User login(String username, String password) {
        return null;
    }

    default User register(String nickname, String password, String password2) {
        return null;
    }

    default User getOneRedis(String id) {
        return null;
    }

    default List<String> getRolesById(Long userId) {
        List<String> list = CollUtil.newArrayList();
        List<String> roles = getRoles(userId);
        roles.stream().forEach(role -> {
            if ((!role.startsWith(Roles.roles))) {
                role = new StringBuffer(Roles.roles).append(role).toString();
            }
            list.add(role);
        });
        return list;
    }

    /**
     * 获取角色
     *
     * @param userId
     * @return
     */
    default List<String> getRoles(Long userId) {
        Logger log = Logger.getLogger(this.getClass().getName());
        log.info("getRoles:{}" + userId);
        return CollUtil.newArrayList();
    }

    default List<String> getPermsById(Long userId) {
        List<String> list = getPerms(userId);
        list.stream().forEach(perm -> {
            if (!perm.startsWith(Roles.perms)) {
                perm = new StringBuffer(Roles.perms).append(perm).toString();
            }
            list.add(perm);
        });
        return list;
    }

    /**
     * 获取菜单权限
     *
     * @param userId
     * @return
     */
    default List<String> getPerms(Long userId) {
        Logger log = Logger.getLogger(this.getClass().getName());
        log.info("getPerms:{}" + userId);
        return CollUtil.newArrayList();
    }

    default List<String> getAnyRolesById(Long userId) {
        List<String> arrayList = CollUtil.newArrayList();
        arrayList.addAll(getRolesById(userId));
        arrayList.addAll(getPermsById(userId));
        return arrayList;
    }

}

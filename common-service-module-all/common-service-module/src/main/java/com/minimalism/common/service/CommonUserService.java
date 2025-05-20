package com.minimalism.common.service;

import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.bean.AbsBean;
import com.minimalism.abstractinterface.service.AbstractUserService;
import com.minimalism.config.AuthorizationConfig;
import com.minimalism.constant.Roles;
import com.minimalism.pojo.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2025/3/7 0:39:57
 * @Description
 */
public interface CommonUserService extends AbsBean {
    @Override
    default void init() {
        debug("[Common]-[Auth]-[init] {}", getClass().getName());
    }

    default boolean isAdmin(String userId) {
        boolean isAdmin = false;
        try {
            User user = SpringUtil.getBean(AbstractUserService.class).getOneRedis(userId);
            List<String> roles = user.getRoles().stream().filter(o -> o.startsWith(Roles.roles)).collect(Collectors.toList());
            isAdmin = SpringUtil.getBean(AuthorizationConfig.class).isAdmin(roles);
        } catch (Exception e) {
            error("error:{}", e);
        }
        return isAdmin;
    }

    default boolean isAdmin() {
        String userId = getUserId();
        return isAdmin(userId);
    }

    String getUserId();


}

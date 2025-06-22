package com.minimalism.auth.service.impl.mp;

import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.aop.abs.bean.AbsBean;
import com.minimalism.mp.abs.service.MpUserService;

/**
 * @Author yan
 * @Date 2025/6/14 01:47:10
 * @Description
 */
public class AuthUserMpService implements MpUserService, AbsBean {
    @Override
    public String getUserId() {
        return SpringUtil.getBean(AuthUserMpService.class).getUserId();
    }
}

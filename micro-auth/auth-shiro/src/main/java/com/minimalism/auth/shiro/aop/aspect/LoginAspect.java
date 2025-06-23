package com.minimalism.auth.shiro.aop.aspect;

import com.minimalism.aop.abs.aspect.AbsLogin;
import com.minimalism.auth.shiro.utils.AuthShiroContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @Author yan
 * @Date 2024/9/27 上午11:03:44
 * @Description
 */
@Aspect
@Slf4j
@Component
public class LoginAspect implements AbsLogin {
    @Override
    public void checkLogin() {
        AuthShiroContextUtil.getUserId();
    }
}

package com.minimalism.auth.service.impl;

import com.minimalism.auth.security.utils.AuthSecurityContextUtil;
import com.minimalism.auth.service.AuthUserService;

/**
 * @Author yan
 * @Date 2025/6/14 01:35:41
 * @Description
 */
public class SecurityAuthUserService implements AuthUserService {
    @Override
    public String getUserId() {
        return AuthSecurityContextUtil.getUserIdNoThrow();
    }
}

package com.minimalism.abstractinterface;

import com.minimalism.abstractinterface.service.filter.AbstractAuthFiler;

/**
 * @Author yan
 * @Date 2025/3/10 3:36:55
 * @Description
 */
public interface AuthorizationFilter extends AbstractAuthFiler, AbstractAuthorizationShiro {
}

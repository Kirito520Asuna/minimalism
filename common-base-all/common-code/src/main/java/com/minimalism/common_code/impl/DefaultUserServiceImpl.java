package com.minimalism.common_code.impl;

import com.minimalism.common_code.abs.service.AbstractUserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

/**
 * @Author yan
 * @Date 2024/5/21 0021 17:26
 * @Description
 */
@Service
@ConditionalOnExpression("!(${authorization.shiro.enable:false} || ${authorization.security.enable:true})")
public class DefaultUserServiceImpl implements AbstractUserService {
}

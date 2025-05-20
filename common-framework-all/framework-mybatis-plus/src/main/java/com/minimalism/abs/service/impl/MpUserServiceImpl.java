package com.minimalism.abs.service.impl;

import com.minimalism.abs.handler.AbstractEntityHandler;
import com.minimalism.abs.service.MpUserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

/**
 * @Author yan
 * @Date 2025/3/6 23:25:17
 * @Description
 */
@Service
@ConditionalOnBean(AbstractEntityHandler.class)
public class MpUserServiceImpl implements MpUserService {
}

package com.minimalism.abstractinterface.service.impl;

import com.minimalism.abstractinterface.handler.AbstractEntityHandler;
import com.minimalism.abstractinterface.service.MpUserService;
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

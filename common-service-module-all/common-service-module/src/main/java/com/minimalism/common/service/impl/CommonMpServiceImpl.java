package com.minimalism.common.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.abstractinterface.handler.AbstractEntityHandler;
import com.minimalism.abstractinterface.service.MpUserService;
import com.minimalism.common.service.CommonUserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * @Author yan
 * @Date 2025/3/7 0:36:20
 * @Description
 */
@ConditionalOnBean(AbstractEntityHandler.class)
@Service @Primary
public class CommonMpServiceImpl implements MpUserService, AbstractBean {
    @Override
    public String getUserId() {
        return SpringUtil.getBean(CommonUserService.class).getUserId();
    }
}

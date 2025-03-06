package com.minimalism.common.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.service.MpUserService;
import com.minimalism.common.service.CommonUserService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * @Author yan
 * @Date 2025/3/7 0:36:20
 * @Description
 */
@Primary
@Service
public class CommonMpServiceImpl implements MpUserService {
    @Override
    public String getUserId() {
        return SpringUtil.getBean(CommonUserService.class).getUserId();
    }
}

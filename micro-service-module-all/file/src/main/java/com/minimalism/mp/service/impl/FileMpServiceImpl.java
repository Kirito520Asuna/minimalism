package com.minimalism.mp.service.impl;

import com.minimalism.abstractinterface.service.MpUserService;
import com.minimalism.utils.shiro.SecurityContextUtil;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * @Author yan
 * @Date 2025/3/6 23:29:35
 * @Description
 */
@Service @Primary
public class FileMpServiceImpl implements MpUserService {
    @Override
    public String getUserId() {
        return SecurityContextUtil.getUserIdNoThrow();
    }
}

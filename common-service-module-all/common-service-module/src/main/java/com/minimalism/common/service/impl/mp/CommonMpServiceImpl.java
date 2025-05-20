package com.minimalism.common.service.impl.mp;

import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.aop.abs.bean.AbsBean;
import com.minimalism.mp.abs.handler.AbsEntityHandler;
import com.minimalism.mp.abs.service.MpUserService;
import com.minimalism.common.service.CommonUserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * @Author yan
 * @Date 2025/3/7 0:36:20
 * @Description
 */
@ConditionalOnBean(AbsEntityHandler.class)
@Service @Primary
public class CommonMpServiceImpl implements MpUserService, AbsBean {
    @Override
    public String getUserId() {
        return SpringUtil.getBean(CommonUserService.class).getUserId();
    }
}

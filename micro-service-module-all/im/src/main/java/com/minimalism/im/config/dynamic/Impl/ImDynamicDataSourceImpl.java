package com.minimalism.im.config.dynamic.Impl;

import com.minimalism.aop.abs.bean.AbsBean;
import com.minimalism.dynamic.abs.AbsDynamicDataSource;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * @Author minimalism
 * @Date 2024/10/25 上午9:06:30
 * @Description
 */
@Primary
@Service
public class ImDynamicDataSourceImpl implements AbsDynamicDataSource, AbsBean {
    @Override
    public String getDataSourceName() {
        return null;
    }
}

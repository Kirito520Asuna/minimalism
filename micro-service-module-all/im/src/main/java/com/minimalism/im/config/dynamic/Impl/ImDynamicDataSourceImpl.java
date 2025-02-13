package com.minimalism.im.config.dynamic.Impl;

import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.abstractinterface.config.dynamic.AbstractDynamicDataSource;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * @Author minimalism
 * @Date 2024/10/25 上午9:06:30
 * @Description
 */
@Primary
@Service
public class ImDynamicDataSourceImpl implements AbstractDynamicDataSource, AbstractBean {
    @Override
    public String getDataSourceName() {
        return null;
    }
}

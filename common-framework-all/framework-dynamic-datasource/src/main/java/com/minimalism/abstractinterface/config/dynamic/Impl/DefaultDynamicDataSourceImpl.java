package com.minimalism.abstractinterface.config.dynamic.Impl;

import com.minimalism.abstractinterface.config.dynamic.AbstractDynamicDataSource;
import com.minimalism.dynamic.config.DynamicDataSourceConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

/**
 * @Author yan
 * @Date 2024/10/25 上午9:00:36
 * @Description
 */
@Service
@ConditionalOnBean(DynamicDataSourceConfig.class)
public class DefaultDynamicDataSourceImpl implements AbstractDynamicDataSource {
}

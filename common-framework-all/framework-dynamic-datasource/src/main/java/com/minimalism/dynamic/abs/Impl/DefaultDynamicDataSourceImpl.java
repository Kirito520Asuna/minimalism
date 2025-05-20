package com.minimalism.dynamic.abs.Impl;

import com.minimalism.dynamic.abs.AbsDynamicDataSource;
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
public class DefaultDynamicDataSourceImpl implements AbsDynamicDataSource {
}

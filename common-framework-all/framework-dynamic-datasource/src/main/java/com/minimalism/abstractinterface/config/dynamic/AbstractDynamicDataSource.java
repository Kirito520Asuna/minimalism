package com.minimalism.abstractinterface.config.dynamic;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.provider.AbstractDataSourceProvider;
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @Author yan
 * @Date 2024/10/25 上午8:05:07
 * @Description
 */
public interface AbstractDynamicDataSource {
    /**
     * 默认分表数据源名称
     */
    String SHARDING_DATA_SOURCE_NAME = "sharding";

    /**
     * 分表数据源名称 重写修改数据源名称
     *
     * @return
     */
    default String getDataSourceName() {
        return SHARDING_DATA_SOURCE_NAME;
    }

    /**
     * 将动态数据源设置为首选的
     * 当spring存在多个数据源时, 自动注入的是首选的对象
     * 设置为主要的数据源之后，就可以支持shardingjdbc原生的配置方式了
     *
     * @return
     */
    default DataSource dataSource(DynamicDataSourceProvider dynamicDataSourceProvider) throws Exception {

        DynamicDataSourceProperties dynamicDataSourceProperties = getDynamicDataSourceProperties();
        DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource();
        dataSource.setPrimary(dynamicDataSourceProperties.getPrimary());
        dataSource.setStrict(dynamicDataSourceProperties.getStrict());
        dataSource.setStrategy(dynamicDataSourceProperties.getStrategy());
        dataSource.setProvider(dynamicDataSourceProvider);
        dataSource.setP6spy(dynamicDataSourceProperties.getP6spy());
        dataSource.setSeata(dynamicDataSourceProperties.getSeata());
        return dataSource;
    }

    default DataSource dataSource() throws Exception {
        DynamicDataSourceProvider dynamicDataSourceProvider = SpringUtil.getBean(DynamicDataSourceProvider.class);
        if (dynamicDataSourceProvider == null) {
            throw new Exception(" 分库分表数据源未配置 ");
        }
        return dataSource(dynamicDataSourceProvider);
    }

    /**
     * 动态 数据源配置
     * 将shardingDataSource放到了多数据源（dataSourceMap）中
     * 注意有个版本的bug，3.1.1版本 不会进入loadDataSources 方法，这样就一直造成数据源注册失败
     */
    default DynamicDataSourceProvider dynamicDataSourceProvider() throws Exception {
        Map<String, DataSourceProperty> datasourceMap = getDynamicDataSourceProperties().getDatasource();
        return new AbstractDataSourceProvider() {
            @Override
            public Map<String, DataSource> loadDataSources() {
                Map<String, DataSource> dataSourceMap = createDataSourceMap(datasourceMap);
                // 将 shardingjdbc 管理的数据源也交给动态数据源管理
                try {
                    org.slf4j.Logger logger = LoggerFactory.getLogger(getClass().getName());
                    String dataSourceName = getDataSourceName();
                    DataSource dataSource = getDataSource();
                    if (ObjectUtil.isNotEmpty(dataSource) && ObjectUtil.isNotEmpty(dataSourceName)) {
                        logger.info("dataSourceName : {}", dataSourceName);
                        dataSourceMap.put(dataSourceName, dataSource);
                        logger.info("DataSource init success");
                    } else {
                        logger.warn("DataSource init error :name can not null and DataSource can not null");
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                Map<String, DataSource> extendedDataSource = getExtendedDataSource();
                if (CollUtil.isNotEmpty(extendedDataSource)) {
                    dataSourceMap.putAll(extendedDataSource);
                }
                return dataSourceMap;
            }
        };
    }

    /**
     * 扩展数据源
     *
     * @return
     */
    default Map<String, DataSource> getExtendedDataSource() {
        return null;
    }

    /**
     * 分库分表数据源
     *
     * @return
     * @throws Exception
     */
    default DataSource getDataSource() throws Exception {
        return SpringUtil.getBean(DataSource.class);
    }

    default DynamicDataSourceProperties getDynamicDataSourceProperties() throws Exception {
        DynamicDataSourceProperties dynamicDataSourceProperties = SpringUtil.getBean(DynamicDataSourceProperties.class);
        if (dynamicDataSourceProperties == null) {
            throw new Exception(" 多数据源未配置 ");
        }
        return dynamicDataSourceProperties;

    }
}

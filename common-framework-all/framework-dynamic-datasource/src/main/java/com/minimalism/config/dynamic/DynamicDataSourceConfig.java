package com.minimalism.config.dynamic;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import com.minimalism.abstractinterface.config.dynamic.AbstractDynamicDataSource;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * @Author yan
 * @Date 2024/7/3 0003 15:59:02
 * @Description 不使用该配置类，使用以下注释在启动类上排除
 * @ComponentScan(excludeFilters = {
 * @ComponentScan.Filter(type = FilterType.REGEX,pattern = "com.yan.config.dynamic.DynamicDataSourceConfig")
 * })
 */

@Configuration
@AutoConfigureBefore({DynamicDataSourceAutoConfiguration.class, SpringBootConfiguration.class})
public class DynamicDataSourceConfig {
    private AbstractDynamicDataSource getAbstractDynamicDataSource() {
        return SpringUtil.getBean(AbstractDynamicDataSource.class);
    }


    public DataSource getDataSource() throws Exception {
        return getAbstractDynamicDataSource().getDataSource();
    }

    /**
     * 将shardingDataSource放到了多数据源（dataSourceMap）中
     * 注意有个版本的bug，3.1.1版本 不会进入loadDataSources 方法，这样就一直造成数据源注册失败
     */

    @Bean
    public DynamicDataSourceProvider initDynamicDataSourceProvider() throws Exception {
        return getAbstractDynamicDataSource().dynamicDataSourceProvider();
    }

    /**
     * 将动态数据源设置为首选的
     * 当spring存在多个数据源时, 自动注入的是首选的对象
     * 设置为主要的数据源之后，就可以支持shardingjdbc原生的配置方式了
     * 以下二选一为bean Primary
     *
     * @return
     */

    @Primary
    @Bean
    public DataSource initDataSource() throws Exception {
        return getAbstractDynamicDataSource().dataSource();
    }

    //initDataSource()重复  选1个即可
    //@Primary
    //@Bean
    public DataSource initDataSource(DynamicDataSourceProvider dynamicDataSourceProvider) throws Exception {
        return getAbstractDynamicDataSource().dataSource(dynamicDataSourceProvider);
    }
}

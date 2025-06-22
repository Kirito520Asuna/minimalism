package com.minimalism.dynamic_datasource.config;

import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.dynamic_datasource.abs.AbsDynamicDataSource;
import com.minimalism.dynamic_datasource.abs.Impl.DefaultDynamicDataSource;
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @Author yan
 * @Date 2025/5/18 16:11:33
 * @Description
 */

@Configuration
@AutoConfigureBefore({DynamicDataSourceAutoConfiguration.class, SpringBootConfiguration.class})
public class DynamicDataSourceConfig {
    @Bean
    @ConditionalOnMissingBean(AbsDynamicDataSource.class)
    public AbsDynamicDataSource dynamicDataSource() {
        return new DefaultDynamicDataSource();
    }
    private AbsDynamicDataSource absDynamicDataSource() {
        return SpringUtil.getBean(AbsDynamicDataSource.class);
    }


    /**
     * 将shardingDataSource放到了多数据源（dataSourceMap）中
     * 注意有个版本的bug，3.1.1版本 不会进入loadDataSources 方法，这样就一直造成数据源注册失败
     */
    @Bean
    public DynamicDataSourceProvider initDynamicDataSourceProvider() throws Exception {
        return absDynamicDataSource().dynamicDataSourceProvider();
    }


    /**
     * 将动态数据源设置为首选的
     * 当spring存在多个数据源时, 自动注入的是首选的对象
     * 设置为主要的数据源之后，就可以支持shardingjdbc原生的配置方式了
     * 以下二选一为bean Primary
     *
     * @return
     */

    @Bean
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource initDataSource() throws Exception {
        return absDynamicDataSource().dataSource();
    }

    //initDataSource()重复  选1个即可
    //@Primary
    //@Bean
    public DataSource initDataSource(DynamicDataSourceProvider dynamicDataSourceProvider) throws Exception {
        return absDynamicDataSource().dataSource(dynamicDataSourceProvider);
    }
}

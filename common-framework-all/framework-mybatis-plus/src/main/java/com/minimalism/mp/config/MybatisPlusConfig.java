package com.minimalism.mp.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.minimalism.abstractinterface.config.AbstractMybatisPlusConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@Configuration
public class MybatisPlusConfig implements AbstractMybatisPlusConfig {
    /**
     * 3.4.0之前的版本用这个
     * @return
     */
   /* @Bean
    public PaginationInterceptor paginationInterceptor(){
        return  new PaginationInterceptor();
    }*/

    /**
     * 分页插件 3.5.X
     * @author
     */
    @Bean @Override
    public PaginationInnerInterceptor paginationInnerInterceptor() {
        return AbstractMybatisPlusConfig.super.paginationInnerInterceptor();
    }

    /**
     * 3.4.0之后提供的拦截器的配置方式
     *
     * @return
     */
    @Bean @Override
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        return AbstractMybatisPlusConfig.super.mybatisPlusInterceptor();
    }

    /**
     * 防止全表更新与删除插件
     * @return
     */
    @Bean
    @Override
    public MybatisPlusInterceptor blockAttackInnerInterceptor() {
        return AbstractMybatisPlusConfig.super.blockAttackInnerInterceptor();
    }

    /**
     * 乐观锁支持
     * @return
     */
    @Bean @Override
    public MybatisPlusInterceptor optimisticLockerInterceptor() {
        return AbstractMybatisPlusConfig.super.optimisticLockerInterceptor();
    }
}

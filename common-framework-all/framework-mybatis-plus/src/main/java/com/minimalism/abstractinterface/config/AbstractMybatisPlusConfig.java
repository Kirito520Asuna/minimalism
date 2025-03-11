package com.minimalism.abstractinterface.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;

/**
 * @Author yan
 * @Date 2024/5/22 0022 9:50
 * @Description
 */
@MapperScan(basePackages = {"com.minimalism.**.dao","com.minimalism.**.**.dao","com.minimalism.**.mapper","com.minimalism.**.**.mapper"})
public interface AbstractMybatisPlusConfig {

    /**
     * 3.4.0之前的版本用这个
     *
     * @return
     */

    default Object paginationInterceptor() {
//        3.4.0之前的版本重写这个方法实列
//        public PaginationInterceptor paginationInterceptor(){
//            return  new PaginationInterceptor();
//        }
        return new Object();
    }

    /**
     * 分页插件 3.5.X
     *
     * @author
     */

    default PaginationInnerInterceptor paginationInnerInterceptor() {
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        paginationInterceptor.setMaxLimit(-1L);
        paginationInterceptor.setDbType(DbType.MYSQL);
        // 开启 count 的 join 优化,只针对部分 left join
        paginationInterceptor.setOptimizeJoin(true);
        return paginationInterceptor;
    }

    /**
     * 3.4.0之后提供的拦截器的配置方式
     *
     * @return
     */

    default MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return mybatisPlusInterceptor;
    }

    /**
     * 防止全表更新与删除插件
     * @return
     */
    default MybatisPlusInterceptor blockAttackInnerInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        return interceptor;
    }

    /**
     * 乐观锁支持
     *
     * @return
     */

    default MybatisPlusInterceptor optimisticLockerInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }
}

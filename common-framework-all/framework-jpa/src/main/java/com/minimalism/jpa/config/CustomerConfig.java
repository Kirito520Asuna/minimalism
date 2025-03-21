package com.minimalism.jpa.config;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2024/6/11 0011 11:11
 * @Description
 */
@Configuration
@ConditionalOnExpression("${jpa.datasource.customer.open:false}")
@EnableTransactionManagement
@EnableJpaRepositories(
        /*事务管理工厂*/
        entityManagerFactoryRef = CustomerConfig.customerEntityManagerFactoryName,
        /*事务管理*/
        transactionManagerRef = CustomerConfig.customerTransactionManagerName,
        /*dao地址*/
        basePackages = {CustomerConfig.customerJpaBasePackage})
public class CustomerConfig {
    final static String customerDataSourcePrefix = "spring.datasource.customer";
    final static String customerDataSourceName = "customerDataSource";
    final static String customerName = "customer";
    final static String customerEntityManagerFactoryName = "customerEntityManagerFactory";
    final static String customerTransactionManagerName = "customerTransactionManager";
    /*dao地址*/
    final static String customerJpaBasePackage = "com.minimalism.jpa.customer.repository";
    /*实体类地址*/
    final static String customerPackageToScan = "com.minimalism.jpa.customer.models";
    /*dao地址*/
    final static String[] customerJpaBasePackages = {customerJpaBasePackage};
    /*实体类地址*/
    final static String[] customerPackagesToScan = {customerPackageToScan};

    final static String[] getCustomerJpaBasePackages() {
        String[] packages = CustomerConfig.customerJpaBasePackages;
        List<String> packageList = Arrays.stream(packages).collect(Collectors.toList());
        packageList.add("com.minimalism.config");
        packages = packageList.toArray(new String[packageList.size()]);
        return packages;
    }

    //@Primary
    @Bean(name = CustomerConfig.customerDataSourceName)
    @ConfigurationProperties(prefix = CustomerConfig.customerDataSourcePrefix)
    public DataSource customerDataSource() {
        return DataSourceBuilder.create().build();
    }

    //@Primary
    @Bean(name = CustomerConfig.customerEntityManagerFactoryName)
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder, @Qualifier(CustomerConfig.customerDataSourceName) DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages(CustomerConfig.customerPackagesToScan)
                .persistenceUnit(CustomerConfig.customerName)
                .build();
    }

    //@Primary
    @Bean(name = CustomerConfig.customerTransactionManagerName)
    public PlatformTransactionManager customerTransactionManager(@Qualifier(CustomerConfig.customerEntityManagerFactoryName) EntityManagerFactory customerEntityManagerFactory) {
        return new JpaTransactionManager(customerEntityManagerFactory);
    }
}
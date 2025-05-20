package com.minimalism.file.beans;

import com.minimalism.aop.abs.bean.AbsBean;
import com.minimalism.file.properties.FileProperties;
import com.minimalism.file.storage.clientAbs.AliyunClient;
import com.minimalism.file.storage.clientAbs.LocalClient;
import com.minimalism.file.storage.platform.AliyunOssStorageClient;
import com.minimalism.file.storage.platform.LocalStorageClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yan
 * @Date 2025/3/9 上午12:40:27
 * @Description
 */
@Configuration
public class BeanConfig implements AbsBean {
    @Bean
    public FileProperties.LocalProperties localProperties() {
        return new FileProperties.LocalProperties();
    }

    @Bean
    @ConditionalOnBean(FileProperties.LocalProperties.class)
    public LocalClient localClient() {
        return new LocalStorageClient();
    }

    @Bean
    @ConditionalOnBean(FileProperties.AliyunOssProperties.class)
    public AliyunClient aliyunClient() {
        return new AliyunOssStorageClient();
    }
}

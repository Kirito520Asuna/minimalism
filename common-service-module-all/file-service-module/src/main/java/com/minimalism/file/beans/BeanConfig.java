package com.minimalism.file.beans;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.file.properties.FileProperties;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @Author yan
 * @Date 2025/3/9 上午12:40:27
 * @Description
 */
@Configuration
public class BeanConfig implements AbstractBean {
    @Bean
    public FileProperties.LocalProperties localProperties() {
        return new FileProperties.LocalProperties();
    }

    //@Bean
    //public NacosDiscoveryProperties nacosDiscoveryProperties() {
    //    NacosDiscoveryProperties nacosDiscoveryProperties = new NacosDiscoveryProperties();
    //    int port = nacosDiscoveryProperties.getPort();
    //    if (port == -1) {
    //        port = SpringUtil.getBean(Environment.class).getProperty("server.port", Integer.class, 8080);
    //        nacosDiscoveryProperties.setPort(port);
    //    }
    //    return nacosDiscoveryProperties;
    //}
}

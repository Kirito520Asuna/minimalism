package com.minimalism.file.config;

import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.bean.AbstractBean;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;

/**
 * @Author yan
 * @Date 2025/3/9 1:57:11
 * @Description
 */
@Configuration
public class FileUploadConfig implements AbstractBean {
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        Environment env = SpringUtil.getBean(Environment.class);
        Integer maxFileSize = env.getProperty("file.upload.maxFileSize", Integer.class, 10);
        Integer maxRequestSize = env.getProperty("file.upload.maxRequestSize", Integer.class, 1024);
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 设置单个文件的最大值
        factory.setMaxFileSize(DataSize.ofMegabytes(maxFileSize)); // 10MB
        // 设置总上传数据的最大值
        factory.setMaxRequestSize(DataSize.ofMegabytes(maxRequestSize)); // 10MB
        return factory.createMultipartConfig();
    }
}

package com.minimalism.file;

import com.minimalism.file.properties.FileProperties;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yan
 * @Date 2025/3/9 上午12:40:27
 * @Description
 */
@Configuration
public class BeanConfig {
    @Bean
    public FileProperties.LocalProperties localProperties() {
        return new FileProperties.LocalProperties();
    }
}

package com.minimalism.common_code.config;

import com.minimalism.aop.abs.bean.AbsBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yan
 * @Date 2025/2/13 17:17:22
 * @Description
 */
@Configuration
public class AppConfig implements AbsBean {

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
        };
    }
}
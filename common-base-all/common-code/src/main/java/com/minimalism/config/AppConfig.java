package com.minimalism.config;

import com.minimalism.abstractinterface.bean.AbstractBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yan
 * @Date 2025/2/13 17:17:22
 * @Description
 */
@Configuration
public class AppConfig implements AbstractBean {

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            System.out.println("扫描到的实体类包：com.example.domain");
            System.out.println("扫描到的 Mapper XML 文件路径：classpath:/mapper/**/*.xml");
        };
    }
}
package com.minimalism.config;

import org.dromara.easyes.starter.register.EsMapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yan
 * @Date 2024/8/13 0013 13:22:37
 * @Description
 */
@Configuration
@EsMapperScan("com.minimalism.elasticsearch")
public class EasyElasticsearchConfig {
}

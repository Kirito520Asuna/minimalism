package com.minimalism.config;

import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yan
 * @Date 2024/10/20 下午11:34:00
 * @Description
 */
@Slf4j
@Configuration
@ConditionalOnExpression("${springdoc.open.default-group-configs.enable:false}")
public class GroupedOpenApiBeanList {
    @Bean
    @ConditionalOnExpression("${springdoc.open.default-group-configs.api:true}")
    public GroupedOpenApi api(){
        log.info("GroupedOpenApi {} init...","api");
       return SwaggerConfig.beanBuildApiGroupedOpenApi();
    }
    @Bean
    @ConditionalOnExpression("${springdoc.open.default-group-configs.jwt:true}")
    public GroupedOpenApi jwt(){
        log.info("GroupedOpenApi {} init...","jwt");
        return SwaggerConfig.beanBuildJwtGroupedOpenApi();
    }
    @Bean
    @ConditionalOnExpression("${springdoc.open.default-group-configs.other:true}")
    public GroupedOpenApi other(){
        log.info("GroupedOpenApi {} init...","other");
        return SwaggerConfig.beanBuildOtherGroupedOpenApi();
    }
}

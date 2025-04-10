package com.minimalism.config.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Author yan
 * @Date 2024/10/21 下午6:04:44
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Component @Slf4j
@ConfigurationProperties(prefix = "security.annotation")
public class SecurityAnnotationConfig {
    /**
     * 是否开启注解 用于测试跳过权限
     */
    private boolean enable = true;
    @PostConstruct
    public void init() {
        log.debug("初始化自定义权限校验");
        log.debug("设置 security.annotation.enable=false 可跳过权限校验 用于本地测试");
    }
}

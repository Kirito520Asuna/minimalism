package com.minimalism.config.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author yan
 * @Date 2024/10/21 下午6:04:44
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Component
@ConfigurationProperties(prefix = "security.annotation")
public class SecurityAnnotationConfig {
    /**
     * 是否开启注解 用于测试跳过权限
     */
    private boolean enable = true;
}

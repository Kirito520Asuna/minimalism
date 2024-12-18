package com.minimalism.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yan
 * @Date 2024/11/5 上午1:25:41
 * @Description
 */
@Configuration
@Data@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationConfig {

    @Value("${authorization.shiro.enable:false}")
    private boolean enableShiro = false;
    @Value("${authorization.security.enable:true}")
    private boolean enableSecurity = true;
}

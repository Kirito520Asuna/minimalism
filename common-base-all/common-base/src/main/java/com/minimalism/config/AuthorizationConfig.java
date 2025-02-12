package com.minimalism.config;

import com.minimalism.constant.Roles;
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
    @Value("${authorization.admin.key:admin}")
    private String adminKey = "admin";

    public String getAdminKey() {
        String admin = adminKey.startsWith(Roles.roles) ? adminKey : new StringBuffer(Roles.roles).append(adminKey).toString();
        return admin;
    }
}

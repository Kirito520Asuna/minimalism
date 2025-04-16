package com.minimalism.config;

import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.constant.Roles;
import com.minimalism.utils.object.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yan
 * @Date 2024/11/5 上午1:25:41
 * @Description
 */
@Configuration
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationConfig  {

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

    public boolean isAdmin(String key) {
        String property = getAdminKey();
        String admin = ObjectUtils.defaultIfEmpty(property, "admin");
        key = ObjectUtils.defaultIfEmpty(key, "");

        if (!key.startsWith(Roles.roles)) {
            key = new StringBuffer(Roles.roles).append(key).toString();
        }

        if (!admin.startsWith(Roles.roles)) {
            admin = new StringBuffer(Roles.roles).append(admin).toString();
        }

        boolean isAdmin = ObjectUtils.equals(admin, key);
        if (isAdmin){
            LoggerFactory.getLogger(this.getClass()).debug("当前用户是管理员");
        }
        return isAdmin;
    }
}

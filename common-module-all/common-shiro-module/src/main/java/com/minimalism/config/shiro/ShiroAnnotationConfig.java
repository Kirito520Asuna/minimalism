package com.minimalism.config.shiro;

import com.minimalism.aop.aspect.ShiroAopAspect;
import com.minimalism.aop.shiro.ShiroPermissions;
import com.minimalism.aop.shiro.ShiroRoles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.aspectj.lang.ProceedingJoinPoint;
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
@ConfigurationProperties(prefix = "shiro.annotation")
public class ShiroAnnotationConfig {
    /**
     * 是否开启注解 用于测试跳过权限
     */
    private boolean enable = true;
    /**
     * 注解逻辑 默认为 and
     * {@link ShiroRoles}
     * {@link ShiroPermissions}
     * and:注解同时满足才允许访问方法
     * @see org.apache.shiro.authz.annotation.Logical
     * @see ShiroAopAspect#around(ProceedingJoinPoint)
     */
    private Logical logical = Logical.AND;

    @PostConstruct
    public void init() {
        log.debug("初始化自定义权限校验");
        log.debug("设置 shiro.annotation.enable=false 可跳过权限校验 用于本地测试");
    }

}

package com.minimalism.aop.shiro;

import org.apache.shiro.authz.annotation.Logical;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author yan
 * @Date 2024/10/21 上午12:45:20
 * @Description
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ShiroRoles {
    /**
     * 需要单个 String 角色名称或多个逗号分隔的角色名称，才能允许方法调用。
     */
    String[] value();
    /**
     * 指定了多个角色时进行权限检查的逻辑操作。AND 是默认值
     * @since 1.1.0
     */
    Logical logical() default Logical.AND;
}

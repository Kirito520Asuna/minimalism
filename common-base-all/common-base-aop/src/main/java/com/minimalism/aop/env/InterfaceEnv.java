package com.minimalism.aop.env;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口环境注解(接口指定环境可用==>用于测试 上线无需修改代码)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface InterfaceEnv {
    String value() default "dev";

    String[] values() default {};

    boolean ignore() default false;
}

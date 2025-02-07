package com.minimalism.aop.aviator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author yan
 * @Date 2024/11/12 上午1:30:05
 * @Description
 */
@Aviator
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AviatorValid {
    /**
     * 解析异常是否抛出异常
     * @return
     */
    boolean throwException() default true;

    String expression(); // Aviator表达式

    String errorMessage() default "参数不符合要求"; // 错误信息

}

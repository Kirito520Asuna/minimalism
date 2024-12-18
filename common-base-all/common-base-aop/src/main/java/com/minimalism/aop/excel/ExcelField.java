package com.minimalism.aop.excel;


import java.lang.annotation.*;

/**
 * excl实现注解，设置别名
 * @author yan
 * @date 2023/4/17 0017 11:54
 */
@Inherited
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelField{

    /**
     * 别名，excel表头名称
     */
    String[] values() default {""};

    /**
     * 是否必传
     * @return
     */
    boolean bool() default false;
}

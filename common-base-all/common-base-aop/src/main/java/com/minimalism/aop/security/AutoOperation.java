package com.minimalism.aop.security;

import com.minimalism.enums.AutoOperationEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface AutoOperation {
    /**
     * 操作类型
     * @return
     */
    AutoOperationEnum operation() default AutoOperationEnum.ADD;
    /**
     * 创建人名称
     * @return
     */
    String createByName() default "createBy";
    /**
     * 更新人名称
     * @return
     */
    String updateByName() default "updateBy";
}

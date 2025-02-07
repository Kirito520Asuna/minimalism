package com.minimalism.aop.aviator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Author yan
 * @Date 2024/11/12 下午2:59:52
 * @Description
 */
@Data @Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class AviatorValidInfo {
    private boolean throwException = true;
    private String expression;
    private String errorMessage;

    public static void main(String[] args) {
        String key = "a";
        String format = String.format("%s!=nil&&%s!=''", key, key);
        System.out.println(format);
    }
}

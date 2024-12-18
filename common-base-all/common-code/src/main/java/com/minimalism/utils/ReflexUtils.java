package com.minimalism.utils;

import java.lang.reflect.Field;

/**
 * @Author yan
 * @Date 2024/7/24 0024 17:13:08
 * @Description
 */
public class ReflexUtils {
    public static <T> Object getPrivateField(T o, String fieldName) {
        try {
            Field field = o.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(o);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

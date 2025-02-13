package com.minimalism.util;

import cn.hutool.core.util.ObjectUtil;
import org.apache.ibatis.type.Alias;

/**
 * @Author yan
 * @Date 2024/11/1 下午9:18:30
 * @Description
 */
@Alias("MyObjectUtils")
public class ObjectUtils extends ObjectUtil {
    public static <T> T defaultIfEmpty(T object, T defaultValue) {
        return isEmpty(object) ? defaultValue : object;
    }
}

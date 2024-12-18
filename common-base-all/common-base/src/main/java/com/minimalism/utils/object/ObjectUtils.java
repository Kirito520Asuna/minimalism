package com.minimalism.utils.object;


import cn.hutool.core.util.ObjectUtil;

/**
 * @Author yan
 * @Date 2024/11/2 下午9:20:38
 * @Description
 */
public class ObjectUtils extends ObjectUtil{
    public static <T> T defaultIfEmpty(T object, T defaultValue) {
        return ObjectUtil.isEmpty(object) ? defaultValue : object;
    }

}

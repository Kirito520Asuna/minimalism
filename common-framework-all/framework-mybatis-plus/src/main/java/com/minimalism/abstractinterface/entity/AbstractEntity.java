package com.minimalism.abstractinterface.entity;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.minimalism.util.ObjectUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author yan
 * @Date 2024/11/1 下午7:04:45
 * @Description
 */
public interface AbstractEntity {
    String ABSTRACT_ENTITY_PARAMS = "params";
    /**
     * 将实体类转成map 优先获取 params, 如果没有则获取实体类
     * @param entity
     * @return
     */
    default Map<String, Object> toParams(Object entity) {
        LinkedHashMap<String, Object> defaultValue = new LinkedHashMap<>();
        Map<String, Object> beanToMap = BeanUtil.beanToMap(ObjectUtils.defaultIfEmpty(entity, defaultValue));
        Map<String, Object> paramsMap = BeanUtil.beanToMap(ObjectUtils.defaultIfEmpty(beanToMap.get(ABSTRACT_ENTITY_PARAMS), defaultValue));
        return ObjectUtil.defaultIfNull(paramsMap, beanToMap);
    }


}

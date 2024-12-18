package com.minimalism.abstractinterface.service;

import cn.hutool.core.collection.CollUtil;
import com.minimalism.pojo.SaltInfo;

import java.util.Collection;


/**
 * @Author yan
 * @Date 2024/5/20 0020 15:35
 * @Description
 */
public interface AbstractApiSaltService {
    /**
     * 获取saltList
     * @return
     */
    default Collection<SaltInfo> getSaltList() {
        return CollUtil.newArrayList();
    }
}

package com.minimalism.abstractinterface.service;

/**
 * @Author yan
 * @Date 2025/3/6 23:23:53
 * @Description
 */
public interface MpUserService  {
    default String getUserId() {
        return null;
    }
}

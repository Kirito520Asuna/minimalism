package com.minimalism.service.impl;

import com.minimalism.abstractinterface.AbstractUserDetailsByShiroService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author yan
 * @Date 2024/5/21 0021 17:30
 * @Description
 */
@Service
public class AbstractUserDetailsByShiroServiceImpl implements AbstractUserDetailsByShiroService {

    @Resource
    @Lazy
    private RedisTemplate redisTemplate;


}

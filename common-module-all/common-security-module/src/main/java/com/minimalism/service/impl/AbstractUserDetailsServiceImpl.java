package com.minimalism.service.impl;

import com.minimalism.service.AbstractUserDetailsService;
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
public class AbstractUserDetailsServiceImpl implements AbstractUserDetailsService {
    //@Value(value = "${login.user.prefix:login_user}")
    //private String loginUserPrefix;
    @Resource
    @Lazy
    private RedisTemplate redisTemplate;

    //@Override
    //public RedisTemplate getRedisTemplate() {
    //    return redisTemplate;
    //}

  /*  @Override
    //@RedisCachePut(cacheName = "login_user", key = "#username",condition = "#re.user!=null",responseAsName = "re")
    public User getLoginUser(String userId) {
        String template = Redis.login_user + ":" + userId;
        Object o = getRedisTemplate().opsForValue().get(template);
        User user = new User();
        try {
            BeanUtil.copyProperties(o, user);
        } catch (Exception e) {
            user = null;
        }
        return user;
    }*/
}

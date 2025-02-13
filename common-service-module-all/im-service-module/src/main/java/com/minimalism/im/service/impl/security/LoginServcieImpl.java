//package com.minimalism.im.service.impl.security;
//
//
//import com.minimalism.im.service.security.ImUserDetailsService;
//import com.minimalism.im.service.security.LoginServcie;
//import com.minimalism.aop.redis.RedisCacheable;
//import com.minimalism.constant.Redis;
//import com.minimalism.im.domain.security.LoginUser;
//import com.minimalism.pojo.User;
//import com.minimalism.user.domain.SysUser;
//import com.minimalism.user.service.SysUserService;
//import com.minimalism.utils.bean.CustomBeanUtils;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.annotation.Resource;
//
///**
// * @Author minimalism
// * @Date 2023/8/7 0007 11:33
// * @Description
// */
//@Service
//public class LoginServcieImpl implements LoginServcie {
//
//    @Resource
//    private SysUserService userService;
//    @Resource
//    private ImUserDetailsService imUserDetailsService;
//    @Resource
//    private PasswordEncoder passwordEncoder;
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public LoginUser register(User user) {
//        user.setUsername(System.currentTimeMillis() + "");
//
//        SysUser sysUser = new SysUser();
//        CustomBeanUtils.copyPropertiesIgnoreNull(user, sysUser);
//        userService.saveOrUpdate(sysUser);
//
//        String id = sysUser.getUserId().toString();
//        String username = sysUser.getUserName();
//        String password = sysUser.getPassword();
//        String nickname = sysUser.getNickName();
//        String image = sysUser.getAvatar();
//
//        User user1 = new User(id, username, nickname, password, image, null);
//        LoginUser loginUser = new LoginUser(user1, null);
//        return loginUser;
//    }
//
//
//    @Override
//    //@Cacheable(cacheNames = "",key = "'" + Redis.login_user + ":' + #id")
//    @RedisCacheable(cacheName=Redis.login_user,key =  "#request.id",classType = LoginUser.class)
//    public LoginUser getOneRedis(String id) {
//        return null;
//    }
//}

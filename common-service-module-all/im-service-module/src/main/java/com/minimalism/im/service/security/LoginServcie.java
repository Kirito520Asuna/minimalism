//package com.minimalism.im.service.security;
//
//
//import cn.hutool.core.bean.BeanUtil;
//import cn.hutool.extra.spring.SpringUtil;
//import com.minimalism.im.domain.security.User;
//import com.minimalism.aop.redis.RedisCacheable;
//import com.minimalism.constant.Redis;
//import com.minimalism.im.domain.security.LoginUser;
//import com.minimalism.pojo.security.UserBase;
//import com.minimalism.service.AbstractUserDetailsService;
//import com.minimalism.utils.bean.CustomBeanUtils;
//
///**
// * @Author yan
// * @Date 2023/8/7 0007 11:33
// * @Description
// */
//public interface LoginServcie {
//    /**
//     * 注册账号
//     *
//     * @param user
//     * @return
//     */
//    LoginUser register(User user);
//
//    /**
//     * 登录
//     *
//     * @param user
//     * @return
//     */
//    default LoginUser login(User user) throws Exception {
//        UserBase login = SpringUtil.getBean(AbstractUserDetailsService.class).
//                loginByUsernamePassword(user.getUsername(), user.getPassword());
//        User user1 = new User();
//        CustomBeanUtils.copyPropertiesIgnoreNull(login.getUser(), user1);
//        LoginUser loginUser = new LoginUser(user1, user1.getRoles());
//        return loginUser;
//    }
//
//
//    /**
//     * 从redis中获取用户信息(redis中存在数据表示在线状态)
//     *
//     * @param id
//     * @return
//     */
//    @RedisCacheable(cacheName= Redis.login_user,key =  "#request.id",classType = LoginUser.class)
//    default LoginUser getOneRedis(String id){
//        return null;
//    }
//
//    /**
//     * 账号退出(移除redis数据)
//     *
//     * @param id
//     * @return
//     */
//    default void logout(String id) {
//        SpringUtil.getBean(AbstractUserDetailsService.class)
//                .logout(id);
//    }
//
//}

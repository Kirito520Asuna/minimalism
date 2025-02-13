//package com.minimalism.im.service.impl.security;
//
//
//import com.baomidou.dynamic.datasource.annotation.DS;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.minimalism.im.domain.security.User;
//import com.minimalism.im.service.security.ImUserDetailsService;
//import com.minimalism.enums.ApiCode;
//import com.minimalism.exception.GlobalCustomException;
//import com.minimalism.constant.DataSourceName;
//import com.minimalism.pojo.UserInfo;
//import com.minimalism.pojo.security.UserBase;
//import com.minimalism.service.impl.AbstractUserDetailsServiceImpl;
//import com.minimalism.user.domain.SysUser;
//import com.minimalism.user.service.SysUserService;
//import com.minimalism.utils.bean.CustomBeanUtils;
//import org.springframework.context.annotation.Primary;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import java.util.List;
//
///**
// * @Author minimalism
// * @Date 2023/8/7 0007 10:57
// * @Description
// */
//@DS(DataSourceName.im)
//@Service @Primary
//public class ImUserDetailsServiceImpl extends AbstractUserDetailsServiceImpl implements ImUserDetailsService {
//    @Resource
//    private RedisTemplate redisTemplate;
//
//
//    @Resource
//    private SysUserService userService;
//
//    @Override
//    public UserInfo getOneByUserName(String userName) {
//
//        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(SysUser::getUserName, userName);
//
//        SysUser user = userService.getOne(lambdaQueryWrapper);
//        if (user == null) {
//            throw new GlobalCustomException(ApiCode.USERNAME_PASSWORD_EORR);
//        }
//
//        Long id = user.getUserId();
//        userName = user.getUserName();
//        String nickname = user.getNickName();
//        String password = user.getPassword();
//        String image = user.getAvatar();
//
//
//        User securityUser = new User(id.toString(), userName, nickname, password, image, null);
//        UserInfo userInfo = new UserInfo();
//        CustomBeanUtils.copyPropertiesIgnoreNull(securityUser, userInfo);
//        userInfo.setRoles(getRolesById(id));
//        //roles权限
//        UserBase loginUser = new UserBase(userInfo, userInfo.getRoles());
//        return userInfo;
//    }
//
//    @Override
//    public List<String> getRoles(Long userId) {
//        return ImUserDetailsService.super.getRoles(userId);
//    }
//
//    @Override
//    public List<String> getPerms(Long userId) {
//        return ImUserDetailsService.super.getPerms(userId);
//    }
//
//    @Override
//    public List<String> getAnyRolesById(Long userId) {
//        return ImUserDetailsService.super.getAnyRolesById(userId);
//    }
//}

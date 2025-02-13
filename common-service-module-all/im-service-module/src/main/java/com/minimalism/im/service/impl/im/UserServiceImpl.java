//package com.minimalism.im.service.impl.im;
//
//
//import com.baomidou.dynamic.datasource.annotation.DS;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.minimalism.constant.DataSourceName;
//import com.minimalism.im.service.im.UserService;
//import com.minimalism.enums.im.ChatType;
//import com.minimalism.im.domain.im.User;
//import com.minimalism.im.mapper.im.UserMapper;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import java.util.List;
//
///**
// * @Author minimalism
// * @Date 2023/8/7 0007 10:36
// * @Description
// */
//@Service @DS(DataSourceName.im)
//public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
//    @Resource
//    private UserMapper dao;
//
//    @Override
//    public List<User> applyList(Long userId) {
//        return dao.applyList(userId);
//    }
//
//    @Override
//    public List<User> getUsers(User user) {
//        List<User> list = dao.getUsers(user);
//        return list;
//    }
//
//    @Override
//    public User getUser(Long chatId, Long userId, ChatType chatType) {
//        return dao.getUser(chatId,userId,chatType);
//    }
//
//    @Override
//    public User getOneUser(Long userId, Long nowUserId) {
//        return dao.getOneUser(userId,nowUserId);
//    }
//}

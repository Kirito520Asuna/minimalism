package com.minimalism.im.utils;

import org.apache.ibatis.type.Alias;

/**
 * @Author yan
 * @Date 2023/9/6 0006 14:24
 * @Description
 */
@Alias("ImEncodePasswordUtils")
public class EncodePasswordUtils extends com.minimalism.shiro.utils.EncodePasswordUtils {
    //public static void encodePassword(User user) throws Exception {
    //    String password = user.getPassword();
    //    String password1 = user.getPassword2();
    //    if (ObjectUtil.equal(password, password1)) {
    //        //密码加密
    //        String encode = encodePassword(password);
    //        user.setPassword(encode);
    //    } else {
    //        throw new GlobalCustomException("密码不一致！");
    //    }
    //}
}

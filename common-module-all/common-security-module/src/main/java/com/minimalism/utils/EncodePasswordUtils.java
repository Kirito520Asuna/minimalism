package com.minimalism.utils;

import com.minimalism.exception.GlobalCustomException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @Author yan
 * @Date 2023/9/6 0006 14:24
 * @Description
 */
public class EncodePasswordUtils {
    public static String encodePassword(String password,String password1) throws Exception {
        if (password.equals(password1)) {
            //密码加密
            String encode = encodePassword(password);
            return encode;
        } else {
            throw new GlobalCustomException("密码不一致");
        }

    }
    // 加密密码
    public static String encodePassword(String plainPassword) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(plainPassword);
    }

    // 比对密码
    public static boolean matchPassword(String plainPassword, String encodedPassword) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(plainPassword, encodedPassword);
    }

    public static void main(String[] args) {
        // 明文密码
        String rawPassword = "myPassword123";

        // 对密码进行加密
        String encodedPassword = encodePassword(rawPassword);
        System.out.println("加密后的密码: " + encodedPassword);

        // 验证密码
        boolean isPasswordMatch = matchPassword(rawPassword, encodedPassword);
        System.out.println("密码匹配: " + isPasswordMatch);
    }
}

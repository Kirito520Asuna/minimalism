package com.minimalism.pojo.shiro;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.minimalism.pojo.User;
import com.minimalism.pojo.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author yan
 * @Date 2023/5/12 0012 16:18
 * @Description
 */
@Data
//@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserBase extends User {
    public UserBase(UserInfo userInfo, List<String> roles){
        super(userInfo, roles);
    }
    public static void main(String[] args) {
        UserBase user = new UserBase(null,CollUtil.newArrayList("asd"));
        System.err.println(JSONUtil.toJsonStr(user, JSONConfig.create().setIgnoreNullValue(false)));
    }
}

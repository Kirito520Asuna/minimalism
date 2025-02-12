package com.minimalism.vo;

import com.minimalism.abstractinterface.bean.AbstractBean;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @Author yan
 * @Date 2024/11/15 上午2:44:16
 * @Description
 */
@Data @Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVo implements Serializable, AbstractBean {
    private String userId;
    private String username;
    private String avatar;
    private List<String> roles;
    private List<String> permissions;
}

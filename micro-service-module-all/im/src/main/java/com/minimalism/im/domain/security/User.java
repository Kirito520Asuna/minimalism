//package com.minimalism.im.domain.security;
//
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.io.Serializable;
//import java.util.List;
//
///**
// * @Author yan
// * @Date 2023/8/7 0007 11:01
// * @Description
// */
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Schema(description = "用户")
//public class User implements Serializable {
//    private static final long serialVersionUID = 7213085245891375147L;
//    @Schema(description = "id")
//    private String id;
//    @Schema(description = "账号")
//    private String username;
//    @Schema(description = "昵称")
//    private String nickname;
//    @Schema(description = "密码")
//    private String password;
//    @Schema(description = "头像")
//    private String image;
//    /**
//     * 权限信息字符串
//     */
//    @Schema(description = "权限字符串")
//    private List<String> roles;
//}

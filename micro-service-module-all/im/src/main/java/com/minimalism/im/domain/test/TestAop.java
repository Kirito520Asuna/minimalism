//package com.minimalism.im.domain.test;
//
//import cn.hutool.json.JSONUtil;
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import lombok.experimental.Accessors;
//import lombok.experimental.SuperBuilder;
//
//import java.io.Serializable;
//import java.time.LocalDateTime;
//
///**
// * @Author yan
// * @Date 2024/9/28 上午1:26:19
// * @Description
// */
//
///**
// * 菜单权限表
// */
//@Schema(description = "菜单权限表")
//@Data
//@Accessors(chain = true)
//@SuperBuilder
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//public class TestAop implements Serializable {
//
//    /**
//     * 创建者
//     */
//    @Schema(description = "创建者")
//    private String createBy;
//
//    /**
//     * 创建时间
//     */
//    @Schema(description = "创建时间")
//    private LocalDateTime createTime;
//
//    /**
//     * 更新者
//     */
//    @Schema(description = "更新者")
//    private String updateBy;
//
//    /**
//     * 更新时间
//     */
//    @Schema(description = "更新时间")
//    private LocalDateTime updateTime;
//
//
//    public static void main(String[] args) {
//        String str = JSONUtil.toJsonStr("sss");
//        boolean typeJSON = JSONUtil.isTypeJSON(str);
//        System.out.println(typeJSON);
//    }
//}
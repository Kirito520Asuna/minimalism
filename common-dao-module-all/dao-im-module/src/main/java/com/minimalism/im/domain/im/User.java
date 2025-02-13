//package com.minimalism.im.domain.im;
//
//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.annotation.TableField;
//import com.baomidou.mybatisplus.annotation.TableId;
//import com.baomidou.mybatisplus.annotation.TableName;
//import com.fasterxml.jackson.annotation.JsonView;
//import com.minimalism.view.BaseJsonView;
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import javax.validation.constraints.NotNull;
//import javax.validation.constraints.Size;
//import java.io.Serializable;
//
///**
// * @Author yan
// * @Date 2023/8/7 0007 10:39
// * @Description
// */
//@Schema
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@TableName(value = "`user`")
//public class User implements Serializable {
//    @TableId(value = "id", type = IdType.AUTO)
//    @Schema(description = "")
//    @NotNull(message = "不能为null", groups = {BaseJsonView.UpdateView.class})
//    @JsonView(value = {BaseJsonView.WebView.class, BaseJsonView.ChatView.class,
//            BaseJsonView.ApplyView.class, BaseJsonView.UserView.class})
//    private Long id;
//
//    @TableField(value = "`username`")
//    @Schema(description = "账号")
//    @Size(max = 100, message = "最大长度要小于 100")
////    @NotNull()
//    @JsonView(value = {BaseJsonView.LoginView.class,
//            BaseJsonView.WebView.class, BaseJsonView.ChatView.class,
//            BaseJsonView.ApplyView.class, BaseJsonView.UserView.class})
//    private String username;
//    @TableField(value = "`nickname`")
//    @Schema(description = "昵称")
//    @Size(max = 100, message = "最大长度要小于 100")
//    @NotNull()
//    @JsonView(value = {BaseJsonView.LoginView.class, BaseJsonView.RegisterView.class,
//            BaseJsonView.WebView.class, BaseJsonView.ChatView.class,
//            BaseJsonView.ApplyView.class, BaseJsonView.UserView.class,BaseJsonView.ApplyAgreeView.class})
//    private String nickname;
//
//    @TableField(value = "`password`")
//    @Schema(description = "")
//    @Size(max = 100, message = "最大长度要小于 100")
//    @NotNull()
//    @JsonView(value = {BaseJsonView.LoginView.class, BaseJsonView.RegisterView.class})
//    private String password;
//    @TableField(exist = false)
//    @Schema(description = "")
//    @Size(max = 100, message = "最大长度要小于 100")
//    @JsonView(value = {BaseJsonView.RegisterView.class})
//    @NotNull()
//    private String password2;
//
//    @TableField(value = "`image`")
//    @Schema(description = "")
//    @Size(max = 255, message = "最大长度要小于 255")
//    @JsonView(value = {BaseJsonView.WebView.class, BaseJsonView.ChatView.class,
//            BaseJsonView.UserView.class,BaseJsonView.ApplyView.class})
//    private String image;
//    @TableField(exist = false)
//    @Schema(description = "聊天窗口id")
//    @JsonView(value = {BaseJsonView.WebView.class,BaseJsonView.ApplyAgreeView.class,BaseJsonView.UserChatView.class})
//    private Long chatId;
//    @TableField(exist = false)
//    @Schema(description = "申请id")
//    @JsonView(value = {BaseJsonView.ApplyView.class})
//    private Long applyId;
//    @TableField(exist = false)
//    @Schema(description = "是否是自己")
//    @JsonView(value = {BaseJsonView.ChatView.class, BaseJsonView.UserView.class})
//    private Boolean isSelf = false;
//
//    @TableField(exist = false)
//    @Schema(description = "是否是好友")
//    @JsonView(value = {BaseJsonView.UserView.class})
//    private Boolean isFriend = false;
//    @TableField(exist = false)
//    @Schema(description = "是否已申请")
//    @JsonView(value = {BaseJsonView.UserView.class})
//    private Boolean isApply = false;
//
//
//    private static final long serialVersionUID = -6484592164850383818L;
//}
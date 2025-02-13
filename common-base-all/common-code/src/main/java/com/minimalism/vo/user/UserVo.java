package com.minimalism.vo.user;

import com.fasterxml.jackson.annotation.JsonView;
import com.minimalism.view.BaseJsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @Author yan
 * @Date 2025/2/13 13:28:37
 * @Description
 */
@Data @NoArgsConstructor
@AllArgsConstructor
public class UserVo implements Serializable {
    @Schema(description = "")
    @NotNull(message = "不能为null", groups = {BaseJsonView.UpdateView.class})
    @JsonView(value = {BaseJsonView.WebView.class, BaseJsonView.ChatView.class,
            BaseJsonView.ApplyView.class, BaseJsonView.UserView.class})
    private Long userId;

    @Schema(description = "账号")
    @Size(max = 100, message = "最大长度要小于 100")
//    @NotNull()
    @JsonView(value = {BaseJsonView.LoginView.class,
            BaseJsonView.WebView.class, BaseJsonView.ChatView.class,
            BaseJsonView.ApplyView.class, BaseJsonView.UserView.class})
    private String userName;
    @Schema(description = "昵称")
    @Size(max = 100, message = "最大长度要小于 100")
    @NotNull()
    @JsonView(value = {BaseJsonView.LoginView.class, BaseJsonView.RegisterView.class,
            BaseJsonView.WebView.class, BaseJsonView.ChatView.class,
            BaseJsonView.ApplyView.class, BaseJsonView.UserView.class,BaseJsonView.ApplyAgreeView.class})
    private String nickName;

    @Schema(description = "")
    @Size(max = 100, message = "最大长度要小于 100")
    @NotNull()
    @JsonView(value = {BaseJsonView.LoginView.class, BaseJsonView.RegisterView.class})
    private String password;
    @Schema(description = "")
    @Size(max = 100, message = "最大长度要小于 100")
    @JsonView(value = {BaseJsonView.RegisterView.class})
    @NotNull()
    private String password2;

    @Schema(description = "")
    @Size(max = 255, message = "最大长度要小于 255")
    @JsonView(value = {BaseJsonView.WebView.class, BaseJsonView.ChatView.class,
            BaseJsonView.UserView.class,BaseJsonView.ApplyView.class})
    private String avatar;
    @Schema(description = "聊天窗口id")
    @JsonView(value = {BaseJsonView.WebView.class,BaseJsonView.ApplyAgreeView.class,BaseJsonView.UserChatView.class})
    private Long chatId;
    @Schema(description = "申请id")
    @JsonView(value = {BaseJsonView.ApplyView.class})
    private Long applyId;
    @Schema(description = "是否是自己")
    @JsonView(value = {BaseJsonView.ChatView.class, BaseJsonView.UserView.class})
    private Boolean isSelf = false;

    @Schema(description = "是否是好友")
    @JsonView(value = {BaseJsonView.UserView.class})
    private Boolean isFriend = false;
    @Schema(description = "是否已申请")
    @JsonView(value = {BaseJsonView.UserView.class})
    private Boolean isApply = false;
}

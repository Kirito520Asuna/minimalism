package com.minimalism.pojo.openfeign;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import com.minimalism.enums.im.MessageType;
import com.minimalism.view.BaseJsonView;
import com.minimalism.vo.user.UserVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author minimalism
 * @Date 2023/8/10 0010 14:22
 * @Description
 */

/**
    * 聊天窗口关联消息
    */
@Schema(description="聊天窗口关联消息")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenfeignChatMessage implements Serializable {
    private static final long serialVersionUID = -8920555648220770635L;
    @Schema(description="")
    @NotNull(message = "不能为null",groups = {BaseJsonView.UpdateView.class})
    @JsonView(value = {BaseJsonView.ChatView.class})
    private Integer id;

    /**
     * 聊天窗口id
     */
    @Schema(description="聊天窗口id")
    @NotNull(message = "聊天窗口id不能为null")
    @JsonView(value = {BaseJsonView.ChatView.class,BaseJsonView.SendMessageView.class})
    private Long chatId;

    /**
     * 消息id
     */
    @Schema(description="消息id")
    @NotNull(message = "消息id不能为null",groups = {BaseJsonView.UpdateView.class})
    @JsonView(value = {BaseJsonView.ChatView.class})
    private Long messageId;
    @Schema(description="发送人id")
    @NotNull(groups = {BaseJsonView.SendMessageView.class})
    @JsonView(value = {BaseJsonView.SendMessageView.class})
    private Long sendUserId;
    @Schema(description="发送人")
    @JsonView(value = {BaseJsonView.ChatView.class})
    private UserVo sendUser;
    @Schema(description="内容")
    @JsonView(value = {BaseJsonView.ChatView.class,BaseJsonView.SendMessageView.class})
    private String content;
    @Schema(description="类型")
    @JsonView(value = {BaseJsonView.ChatView.class,BaseJsonView.SendMessageView.class})
    @Enumerated(value = EnumType.STRING)
    private MessageType type;
    @Schema(description="时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

}
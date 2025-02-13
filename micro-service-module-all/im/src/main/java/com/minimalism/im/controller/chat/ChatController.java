package com.minimalism.im.controller.chat;

import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.annotation.JsonView;
import com.minimalism.aop.log.SysLog;
import com.minimalism.enums.im.ChatType;
import com.minimalism.im.domain.chat.ChatMessage;
import com.minimalism.im.service.chat.ChatMessageService;
import com.minimalism.result.Result;
import com.minimalism.user.domain.SysUser;
import com.minimalism.user.service.SysUserService;
import com.minimalism.utils.bean.CustomBeanUtils;
import com.minimalism.view.BaseJsonView;
import com.minimalism.vo.user.UserVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static com.minimalism.result.Result.ok;

/**
 * @Author minimalism
 * @Date 2023/8/9 0009 14:53
 * @Description
 */
@Tag(name = "聊天管理")
@RestController
@RequestMapping(value = {"/api/chat/", "/jwt/chat/", "/chat/"})
public class ChatController {
    @Resource
    private ChatMessageService chatMessageService = SpringUtil.getBean(ChatMessageService.class);
    @Resource
    private SysUserService userService = SpringUtil.getBean(SysUserService.class);

    @GetMapping("get/list")
    @Operation(summary = "获取聊天记录")
    @SysLog(title = "获取聊天记录")
    @JsonView(value = {BaseJsonView.ChatView.class})
    public Result<List<ChatMessage>> getList(@RequestParam Long chatId, @RequestParam Long userId) {
        List<ChatMessage> list = chatMessageService.getList(chatId, userId);
        return ok(list);
    }

    @PostMapping(value = "send/message")
    @Operation(summary = "发送消息")
    @SysLog(title = "发送消息")
    public Result sendMessage(@JsonView(value = {BaseJsonView.SendMessageView.class})
                              @Validated(value = {BaseJsonView.SendMessageView.class})
                              @RequestBody ChatMessage chatMessage) {
        chatMessageService.sendMessage(chatMessage);
        return ok();
    }

    @GetMapping("getUser")
    @Operation(summary = "获取接收者信息")
    @SysLog(title = "获取接收者信息")
    @JsonView(value = {BaseJsonView.ChatView.class})
    public Result<UserVo> getUser(@RequestParam Long chatId,
                                  @RequestParam Long userId,
                                  @RequestParam ChatType chatType) {
        SysUser user = userService.getUser(chatId, userId, chatType);
        UserVo userVo = new UserVo();
        CustomBeanUtils.copyPropertiesIgnoreNull(user, userVo);
        return ok(userVo);
    }
}

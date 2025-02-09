package com.minimalism.controller;

import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.aop.log.SysLog;
import com.minimalism.domain.Message;
import com.minimalism.endpoint.WebSocketEndpoint;
import com.minimalism.result.Result;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

/**
 * @Author minimalism
 * @Date 2025/2/6 1:50:07
 * @Description
 */

@RequestMapping({"/message", "/api/message"})
@RestController
public class MessageController {
    @GetMapping
    public Result<String> test() {
        return Result.ok();
    }
    @SysLog
    @Operation(summary = "发送消息")
    @PostMapping("/send")
    public Result<String> sendLocalMessage(@RequestBody Message message) {
        SpringUtil.getBean(WebSocketEndpoint.class).sendLocalMessage(message.getSenderId(),message);
        return Result.ok();
    }
}

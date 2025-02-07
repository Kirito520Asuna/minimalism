package com.minimalism.controller;

import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.domain.Message;
import com.minimalism.endpoint.WebSocketEndpoint;
import com.minimalism.result.Result;

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

    @PostMapping("/send")
    public Result<String> sendLocalMessage(@RequestBody Message message) {
        SpringUtil.getBean(WebSocketEndpoint.class).sendLocalMessage(message.getSenderId(),message);
        return Result.ok();
    }
}

package com.minimalism.controller;

import com.minimalism.aop.log.SysLog;
import com.minimalism.pojo.CaptchaInfo;
import com.minimalism.result.Result;
import com.minimalism.validate_code.service.ValidateCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author yan
 * @Date 2024/11/11 下午6:33:12
 * @Description
 */
@RestController
@Tag(name = "验证码模块")
@RequestMapping(value = {"/api/captcha","/captcha"})
public class CaptchaController implements AbstractBaseController {
    @Resource
    private ValidateCodeService validateCodeService;
        @SysLog
    @Operation(summary = "获取验证码")
    @GetMapping("/getImgCode")
    public Result<CaptchaInfo> getCaptcha() {
        CaptchaInfo captcha = validateCodeService.createCaptcha();
        return ok(captcha);
    }
}

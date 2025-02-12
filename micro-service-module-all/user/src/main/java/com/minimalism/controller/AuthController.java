package com.minimalism.controller;

import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.service.AbstractLoginService;
import com.minimalism.aop.aviator.AviatorNotBlank;
import com.minimalism.aop.aviator.AviatorValid;
import com.minimalism.aop.aviator.AviatorValids;
import com.minimalism.aop.log.SysLog;
import com.minimalism.dto.LoginDto;
import com.minimalism.dto.RegisterDto;
import com.minimalism.pojo.TokenInfo;
import com.minimalism.pojo.User;
import com.minimalism.pojo.UserInfo;
import com.minimalism.result.Result;
import com.minimalism.validate_code.service.ValidateCodeService;
import com.minimalism.utils.shiro.SecurityContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

/**
 * @Author yan
 * @Date 2024/11/5 下午11:04:45
 * @Description
 */
@RestController
@Tag(name = "登陆注册模块")
@RequestMapping(value = {"/api/auth", "/jwt/auth"})
public class AuthController implements AbstractBaseController {
    /**
     * 查询用户信息列表
     */
    @SysLog(title = "登陆")
    @Operation(summary = "登陆")
    @PostMapping("/login")
    @AviatorValids(
            notBlanks = {
                    @AviatorNotBlank(key = "dto.username", errorMessage = "用户名不能为空"),
                    @AviatorNotBlank(key = "dto.password", errorMessage = "密码不能为空"),
                    @AviatorNotBlank(key = "dto.code", errorMessage = "验证码不能为空"),
                    @AviatorNotBlank(key = "dto.uuid", errorMessage = "验证码唯一值不能为空"),
            },
            values = {
                    @AviatorValid(expression = "dto.captchaEnabled==true && dto.code!=null && dto.code!=''", errorMessage = "验证码不能为空"),
            }
    )
    public Result<TokenInfo> login(@RequestBody LoginDto dto, HttpServletResponse response) {
        SpringUtil.getBean(ValidateCodeService.class)
                .checkCaptcha(dto.getCode(), dto.getUuid());

        UserInfo userInfo = new UserInfo().setPassword(dto.getPassword()).setUsername(dto.getUsername());
        TokenInfo tokenInfo = SpringUtil.getBean(AbstractLoginService.class).login(userInfo);

        response.setHeader(tokenInfo.getTokenName(), tokenInfo.getToken());
        if (tokenInfo.getEnableTwoToken()) {
            response.setHeader(tokenInfo.getRefreshTokenName(), tokenInfo.getRefreshToken());
        }
        return ok(tokenInfo);
    }

    @SysLog(title = "注册")
    @Operation(summary = "注册")
    @PostMapping("/register")
    @AviatorValids(values = {
            @AviatorValid(expression = "dto.password != dto.password2", errorMessage = "密码不一致")
    })
    public Result<User> register(@Validated @NotNull @RequestBody RegisterDto dto) {
        User register = SpringUtil.getBean(AbstractLoginService.class)
                .register(dto.getNickname(), dto.getPassword(), dto.getPassword2());
        return ok(register);
    }

    @SysLog(title = "登出")
    @Operation(summary = "登出")
    @PostMapping("/logout")
    public Result logout() {
        SecurityContextUtil.logout();
        return ok();
    }
}

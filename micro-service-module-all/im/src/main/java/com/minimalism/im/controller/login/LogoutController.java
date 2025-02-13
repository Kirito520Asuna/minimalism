//package com.minimalism.im.controller.login;
//
//import com.minimalism.aop.log.SysLog;
//import com.minimalism.enums.Header;
//import com.minimalism.im.service.security.LoginServcie;
//import com.minimalism.result.Result;
//import com.minimalism.security.SecurityContextUtil;
//import com.minimalism.utils.jwt.JwtUtils;
//import io.jsonwebtoken.Claims;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//
//import static com.minimalism.result.Result.ok;
//
//
///**
// * @Author minimalism
// * @Date 2023/8/7 0007 16:11
// * @Description
// */
//@Tag(name = "账号退出")
//@RestController
//@RequestMapping(value = {"/","/jwt/","/test/"})
//public class LogoutController {
//    @Resource
//    private LoginServcie loginServcie;
//
//    @GetMapping("logout")
//    @Operation(summary = "退出")
//    @SysLog(title = "退出")
//    public Result logout(HttpServletRequest request) throws Exception {
//        String token = request.getHeader(Header.TOKEN.getName());
//        token = !StringUtils.hasText(token) ? request.getHeader(Header.AUTHORIZATION.getName()) : token;
//        Claims claims = JwtUtils.parseJWT(token);
//        String id = claims.getSubject();
//        String userId = SecurityContextUtil.getUserId();
////        Long id = Long.parseLong(subject);
//        loginServcie.logout(id);
//        return ok();
//    }
//
//}

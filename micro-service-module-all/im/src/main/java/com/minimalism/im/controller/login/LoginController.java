//package com.minimalism.im.controller.login;
//
//import cn.hutool.core.lang.UUID;
//import cn.hutool.core.util.IdUtil;
//import cn.hutool.core.util.ObjectUtil;
//import cn.hutool.extra.spring.SpringUtil;
//import com.fasterxml.jackson.annotation.JsonView;
//
//import com.minimalism.dto.user.UserDto;
//import com.minimalism.im.domain.security.User;
//import com.minimalism.im.domain.test.TestAop;
//import com.minimalism.im.utils.EncodePasswordUtils;
//import com.minimalism.aop.log.SysLog;
//import com.minimalism.aop.security.AutoOperation;
//import com.minimalism.enums.Header;
//import com.minimalism.im.domain.security.LoginUser;
//import com.minimalism.im.service.security.LoginServcie;
//import com.minimalism.result.Result;
//import com.minimalism.utils.bean.CustomBeanUtils;
//import com.minimalism.utils.jwt.JwtUtils;
//import com.minimalism.view.BaseJsonView;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.util.ObjectUtils;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.annotation.Resource;
//import javax.servlet.ServletOutputStream;
//import javax.servlet.http.HttpServletResponse;
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//
//import static com.minimalism.result.Result.ok;
//
//
///**
// * @Author minimalism
// * @Date 2023/8/4 0004 14:10
// * @Description
// */
//@Slf4j
//@Tag(name = "账号登录注册管理")
//@RestController
//@RequestMapping(value = {"/api/auth/user/", "/auth/user/"})
//public class LoginController {
//    @Resource
//    private LoginServcie loginServcie;
//
//    @PostMapping("login")
//    @Operation(summary = "登录")
//    @SysLog(title = "登录")
//    public Result login(@Validated(BaseJsonView.LoginView.class)
//                        @JsonView(BaseJsonView.LoginView.class)
//                        @RequestBody User user,
//                        HttpServletResponse response) throws Exception {
//        LoginUser login = loginServcie.login(user);
//        String id = login.getUser().getId();
//
//        String jwt = JwtUtils.createJWT(id);
//        Map<String, String> map = new HashMap<>();
//        map.put(Header.TOKEN.getName(), jwt);
//
//        response.setHeader(Header.TOKEN.getName(), jwt);
//        response.setHeader(Header.AUTHORIZATION.getName(), jwt);
//        ServletOutputStream out;
//        out = response.getOutputStream();
//        out.flush();
//        return ok(map);
//    }
//
//    @PostMapping("register")
//    @Operation(summary = "注册")
//    @SysLog(title = "注册")
//    @JsonView(BaseJsonView.RegisterView.class)
//    public Result<LoginUser> register(@Validated(BaseJsonView.RegisterView.class)
//                                      @JsonView(BaseJsonView.RegisterView.class)
//                                      @RequestBody UserDto dto) throws Exception {
//        String password = dto.getPassword();
//        String password2 = dto.getPassword2();
//        boolean a = !ObjectUtils.isEmpty(password);
//        boolean b = !ObjectUtils.isEmpty(password2);
//        dto.setPassword(EncodePasswordUtils.encodePassword(password, password2));
//        if (ObjectUtil.isEmpty(dto.getNickName())) {
//            dto.setNickName(UUID.fastUUID().toString());
//        }
//        User user = new User();
//        CustomBeanUtils.copyPropertiesIgnoreNull(dto, user);
//        LoginUser login = loginServcie.register(user);
//        login.setPassword(password);
//        return ok(login);
//    }
//
//    @GetMapping("get")
//    @Operation(summary = "测试")
//    @SysLog(title = "测试", module = "#id")
//    public Result get(@RequestParam String id) {
//        TestAop testAop = new TestAop().setCreateTime(LocalDateTime.now());
//        SpringUtil.getBean(LoginController.class).testAop(testAop);
//        return ok(id);
//    }
//
//    @AutoOperation
//    public void testAop(TestAop aop) {
//        log.info("TestAop : {}", aop);
//    }
//
//    @PostMapping("post")
//    @Operation(summary = "测试2")
//    @SysLog(title = "测试2", module = "#id")
//    public Result get2(@RequestParam String id, @RequestPart MultipartFile file) {
//        return ok("post");
//    }
//
//    public static void main(String[] args) {
//        long l = IdUtil.getSnowflake().nextId();
//        long userId = IdUtil.getSnowflake(1, 1).nextId();
//        System.out.println(l);
//        System.out.println(userId);
//        System.out.println(String.valueOf(userId).length());
//    }
//}

package com.minimalism.im.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonView;
import com.minimalism.aop.log.SysLog;
import com.minimalism.im.domain.im.Apply;
import com.minimalism.im.domain.im.Friend;
import com.minimalism.im.service.im.ApplyService;
import com.minimalism.im.service.im.FriendService;
import com.minimalism.result.Result;
import com.minimalism.user.domain.SysUser;
import com.minimalism.user.service.SysUserService;
import com.minimalism.utils.bean.CustomBeanUtils;
import com.minimalism.utils.object.ObjectUtils;
import com.minimalism.view.BaseJsonView;
import com.minimalism.vo.user.UserVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static com.minimalism.result.Result.fail;
import static com.minimalism.result.Result.ok;


/**
 * @Author minimalism
 * @Date 2023/8/9 0009 14:53
 * @Description
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping(value = {"/api/user/", "/jwt/user/", "/user/"})
public class UserController {
    @Resource
    private SysUserService userService;
    @Resource
    private ApplyService applyService;
    //@Resource
    //private LoginServcie loginServcie;
    @Resource
    private FriendService friendService;

    private static UserVo createUserVo(SysUser user) {
        UserVo userVo = new UserVo();
        CustomBeanUtils.copyPropertiesIgnoreNull(user, userVo);
        return userVo;
    }

/*    @GetMapping("getOne")
    @Operation(summary = "获取当前登录用户")
    @SysLog(title = "获取当前登录用户")
    @JsonView(value = {BaseJsonView.WebView.class})
    public Result<UserVo> getOne(
            HttpServletRequest request) throws Exception {
        String token = request.getHeader(Header.TOKEN.getName());
        token = !StringUtils.hasText(token) ? request.getHeader(Header.AUTHORIZATION.getName()) : token;
        Claims claims = JwtUtils.parseJWT(token);
        String id = claims.getSubject();
//        Long id = Long.parseLong(subject);
        LoginUser oneRedis = loginServcie.getOneRedis(id);
        User user = oneRedis.getUser();
        SysUser user1 = userService.getById(user.getId());

        UserVo userVo = createUserVo(user1);

        return ok(userVo);
    }*/

/*
    @GetMapping("getUser")
    @Operation(summary = "获取用户")
    @SysLog(title = "获取用户")
    @JsonView(value = {BaseJsonView.WebView.class})
    public Result<UserVo> getUser(@Parameter(description = "用户id") @RequestParam Long userId) {
        SysUser user = userService.getById(userId);
        UserVo userVo = createUserVo(user);
        return ok(userVo);
    }



    @GetMapping("getOneUser")
    @Operation(summary = "获取用户")
    @SysLog(title = "获取用户")
    @JsonView(value = {BaseJsonView.UserChatView.class})
    public Result<UserVo> getOneUser(@Parameter(description = "用户id") @RequestParam Long userId,
                                   @Parameter(description = "当前登录用户id") @RequestParam Long nowUserId) {
        SysUser user = userService.getOneUser(userId,nowUserId);
        UserVo userVo = createUserVo(user);
        return ok(userVo);
    }
*/

    @GetMapping("getFriends")
    @Operation(summary = "获取好友")
    @SysLog(title = "获取好友")
    @JsonView(value = {BaseJsonView.WebView.class})
    public Result<List<UserVo>> getFriends(@Parameter(description = "用户id") @RequestParam Long userId,
                                         @Parameter(description = "账号或者昵称") @RequestParam(required = false) String keyword) {
        List<UserVo> users = friendService.getFriends(userId, keyword);
        return ok(users);
    }

/*
    @GetMapping("getUsers")
    @Operation(summary = "搜索用户")
    @SysLog(title = "搜索用户")
    @JsonView(value = {BaseJsonView.UserView.class})
    public Result<List<UserVo>> getUsers(@Parameter(description = "用户id") @RequestParam Long userId,
                                       @Parameter(description = "账号或者昵称") @RequestParam(required = false) String keyword) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setUserName(keyword);
        user.setNickName(keyword);
        List<UserVo> users = userService.getUsers(user)
                .stream().filter(ObjectUtils::isNotEmpty)
                .map(sysUser -> createUserVo(sysUser)).collect(Collectors.toList());

        return ok(users);
    }

*/

    @PostMapping("apply")
    @Operation(summary = "申请添加好友")
    @SysLog(title = "申请添加好友")
    public Result apply(@Validated(BaseJsonView.ApplyView.class)
                        @JsonView(BaseJsonView.ApplyView.class)
                        @RequestBody Apply apply) {
        LambdaQueryWrapper<Apply> la = new LambdaQueryWrapper<>();
        la.eq(Apply::getUid, apply.getUid()).eq(Apply::getTid, apply.getTid());
        int count = applyService.count(la);

        if (count > 0){
            return fail("该用户已向您发起了好友申请！");
        }

        LambdaQueryWrapper<Friend> friendLambdaQueryWrapper = new LambdaQueryWrapper<>();
        friendLambdaQueryWrapper.eq(Friend::getUid, apply.getUid()).eq(Friend::getFid, apply.getTid());
        count = friendService.count(friendLambdaQueryWrapper);
        if (count > 0) {
            return fail("已添加过该好友！");
        }
        applyService.saveOrUpdate(apply);
        return ok();
    }

    @PostMapping("apply/agree")
    @Operation(summary = "是否同意添加好友")
    @SysLog(title = "是否同意添加好友")
    @JsonView(BaseJsonView.ApplyAgreeView.class)
    public Result<UserVo> applyAgree(@Validated(BaseJsonView.UpdateView.class)
                             @JsonView(BaseJsonView.UpdateView.class)
                             @RequestBody Apply apply) {
        UserVo user = applyService.applyAgree(apply);
        return ok(user);
    }

    @GetMapping("apply/list")
    @Operation(summary = "获取申请列表")
    @SysLog(title = "获取申请列表")
    @JsonView(BaseJsonView.ApplyView.class)
    public Result<List<UserVo>> applyList(@RequestParam Long userId) {
        List<SysUser> list = userService.applyList(userId);
        List<UserVo> userVoList = list.stream().filter(ObjectUtils::isNotEmpty)
                .map(sysUser -> createUserVo(sysUser)).collect(Collectors.toList());
        return ok(userVoList);
    }

}

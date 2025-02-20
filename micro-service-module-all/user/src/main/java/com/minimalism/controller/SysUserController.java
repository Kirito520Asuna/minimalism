package com.minimalism.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.annotation.JsonView;
import com.minimalism.abstractinterface.service.AbstractLoginService;
import com.minimalism.abstractinterface.service.AbstractUserService;
import com.minimalism.enums.Header;
import com.minimalism.pojo.User;
import com.minimalism.pojo.UserInfo;
import com.minimalism.user.domain.SysUser;
import com.minimalism.user.service.SysUserService;
import com.minimalism.util.ObjectUtils;
import com.minimalism.utils.bean.CustomBeanUtils;
import com.minimalism.utils.jwt.JwtUtils;
import com.minimalism.utils.poi.ExcelUtil;
import com.minimalism.utils.shiro.SecurityContextUtil;
import com.minimalism.view.BaseJsonView;
import com.minimalism.vo.UserInfoVo;
import com.minimalism.vo.user.UserVo;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.minimalism.aop.log.SysLog;
import com.minimalism.enums.BusinessType;
import com.minimalism.aop.shiro.ShiroPermissions;

import com.minimalism.result.Result;
import com.minimalism.result.ResultPage;
import com.minimalism.util.PageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.minimalism.result.Result.ok;

/**
 * @Author yan
 * @Date 2024/10/3 下午9:14:44
 * @Description
 */

@RestController
@Tag(name = "用户模块")
@RequestMapping(value = {"/api/user/", "/jwt/user/", "/user/"})
public class SysUserController implements AbstractBaseController {
    @Resource
    private SysUserService sysUserService;

    /**
     * 查询用户信息列表
     */
    @SysLog
    @Operation(summary = "查询用户信息列表")
    @ShiroPermissions("system:user:list")
    @GetMapping("list")
    public Result<ResultPage<SysUser>> list(@RequestParam(defaultValue = "1") Long pageNumber,
                                            @RequestParam(defaultValue = "10") Long pageSize) {

        SysUser sysUser = new SysUser();
        Map<String, Object> params = sysUser.toParams();
        params.put(PageUtils.pageNumber, pageNumber);
        params.put(PageUtils.pageSize, pageSize);
        PageUtils.startPage(params);
        List<SysUser> list = sysUserService.selectSysUserList(sysUser);
        return listToPage(list);
    }

    /**
     * 导出用户信息列表
     */
    @SysLog(businessType = BusinessType.EXPORT)
    @Operation(summary = "导出用户信息列表")
    @ShiroPermissions("system:user:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysUser sysUser) {
        PageUtils.startPage(sysUser.getParams());
        List<SysUser> list = sysUserService.selectSysUserList(sysUser);
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.exportExcel(response, list, "用户信息数据");
    }

    /**
     * 获取用户信息详细信息
     */
    @SysLog(title = "获取用户信息详细信息")
    @Operation(summary = "获取用户信息详细信息")
    @GetMapping(value = "getUserInfo")
    public Result<UserInfoVo> getUserInfo(@RequestParam(required = false) Long userId) {
        if (ObjectUtils.isEmpty(userId)) {
            userId = Long.parseLong(SecurityContextUtil.getUserId());
        }
        AbstractUserService bean = SpringUtil.getBean(AbstractUserService.class);
        List<String> roles = bean.getRolesById(userId);
        List<String> perms = bean.getPermsById(userId);

        SysUser user = sysUserService.selectSysUserByUserId(userId);
        UserInfoVo userInfoVo = new UserInfoVo()
                .setUserId(String.valueOf(user.getUserId()))
                .setAvatar(user.getAvatar())
                .setUsername(user.getUserName())
                .setPermissions(perms)
                .setRoles(roles);
        return ok(userInfoVo);
    }

    /**
     * 获取用户信息详细信息
     */
    @SysLog
    @Operation(summary = "获取用户信息详细信息")
    @ShiroPermissions("system:user:query")
    @GetMapping(value = "{userId}")
    public Result getInfo(@PathVariable("userId") Long userId) {
        return ok(sysUserService.selectSysUserByUserId(userId));
    }

    /**
     * 新增用户信息
     */
    @SysLog(businessType = BusinessType.INSERT)
    @Operation(summary = "新增用户信息")
    @ShiroPermissions("system:user:add")
    @PostMapping
    public Result add(@RequestBody SysUser sysUser) {
        return ok(sysUserService.save(sysUser));
    }

    /**
     * 修改用户信息
     */
    @SysLog(businessType = BusinessType.UPDATE)
    @Operation(summary = "修改用户信息")
    @ShiroPermissions("system:user:edit")
    @PutMapping
    public Result edit(@RequestBody SysUser sysUser) {
        return ok(sysUserService.updateById(sysUser));
    }

    /**
     * 删除用户信息
     */
    @SysLog(businessType = BusinessType.DELETE)
    @Operation(summary = "删除用户信息")
    @ShiroPermissions("system:user:remove")
    @DeleteMapping("{userIds}")
    public Result remove(@PathVariable Long[] userIds) {
        List<Long> userIdList = Arrays.stream(userIds).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(userIdList)) {
            sysUserService.deleteByUserIds(userIdList);
        }
        return ok();
    }

    //=========================================================================================
    private static UserVo createUserVo(SysUser user) {
        UserVo userVo = new UserVo();
        CustomBeanUtils.copyPropertiesIgnoreNull(user, userVo);
        return userVo;
    }

    @GetMapping("getOne")
    @Operation(summary = "获取当前登录用户")
    @SysLog(title = "获取当前登录用户")
    @JsonView(value = {BaseJsonView.WebView.class})
    public Result<UserVo> getOne(
            HttpServletRequest request) throws Exception {
        String token = request.getHeader(Header.TOKEN.getName());
        token = !StringUtils.hasText(token) ? request.getHeader(Header.AUTHORIZATION.getName()) : token;
        Claims claims = JwtUtils.parseJWT(token);
        String id = claims.getSubject();

        User oneRedis = SpringUtil.getBean(AbstractLoginService.class).getOneRedis(id);
        UserInfo user = oneRedis.getUser();
        SysUser user1 = sysUserService.getById(user.getId());
        UserVo userVo = createUserVo(user1);

        return ok(userVo);
    }

    @GetMapping("getUser")
    @Operation(summary = "获取用户")
    @SysLog(title = "获取用户")
    @JsonView(value = {BaseJsonView.WebView.class})
    public Result<UserVo> getUser(@Parameter(description = "用户id") @RequestParam Long userId) {
        SysUser user = sysUserService.getById(userId);
        UserVo userVo = createUserVo(user);
        return ok(userVo);
    }

    @GetMapping("getOneUser")
    @Operation(summary = "获取用户")
    @SysLog(title = "获取用户")
    @JsonView(value = {BaseJsonView.UserChatView.class})
    public Result<UserVo> getOneUser(@Parameter(description = "用户id") @RequestParam(required = false) Long userId,
                                     @Parameter(description = "当前登录用户id") @RequestParam(required = false) Long nowUserId) {
        if (userId == null && nowUserId == null) {
            String userIdStr = SecurityContextUtil.getUserId();
            Long userIdLong = Long.parseLong(userIdStr);
            userId = nowUserId = userIdLong;
        }

        SysUser user = sysUserService.getOneUser(userId, nowUserId);
        UserVo userVo = createUserVo(user);
        return ok(userVo);
    }

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
        List<UserVo> users = sysUserService.getUsers(user)
                .stream().filter(com.minimalism.utils.object.ObjectUtils::isNotEmpty)
                .map(sysUser -> createUserVo(sysUser)).collect(Collectors.toList());

        return ok(users);
    }
}

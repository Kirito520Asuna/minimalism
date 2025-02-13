package com.minimalism.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.minimalism.constant.DataSourceName;
import com.minimalism.enums.im.ChatType;
import com.minimalism.user.service.SysRoleMenuService;
import com.minimalism.user.service.SysRoleService;
import com.minimalism.user.service.SysUserRoleService;
import com.minimalism.user.service.SysUserService;
import com.minimalism.aop.redis.RedisCachePut;
import com.minimalism.aop.redis.RedisCacheable;
import com.minimalism.constant.Redis;
import com.minimalism.enums.SysUserStatus;
import com.minimalism.exception.GlobalCustomException;
import com.minimalism.pojo.User;
import com.minimalism.pojo.UserInfo;
import com.minimalism.user.domain.SysUserRole;
import com.minimalism.utils.EncodePasswordUtils;
import com.minimalism.utils.enums.EnumUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minimalism.user.domain.SysUser;

import java.util.stream.Collectors;

import com.minimalism.user.mapper.SysUserDao;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @Author minimalism
 * @Date 2024/9/28 上午1:26:19
 * @Description
 */
@Service @DS(DataSourceName.user)
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUser> implements SysUserService {
    @Resource
    private SysUserDao sysUserDao;
    @Resource
    private SysUserRoleService sysUserRoleService;
    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private SysRoleMenuService sysRoleMenuService;

    @Override
    public UserInfo getOneByUserName(String userName) {
        UserInfo userInfo = null;
        SysUser sysUser = getOne(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getUserName, userName));
        if (ObjectUtil.isNotEmpty(sysUser)) {
            userInfo = new UserInfo();
            SysUserStatus userStatus = EnumUtils.getEnumByPrivateFieldName(SysUserStatus.class, sysUser.getStatus(), "status");

            userInfo.setId(String.valueOf(sysUser.getUserId()))
                    .setUsername(sysUser.getUserName())
                    .setNickname(sysUser.getNickName())
                    .setImage(sysUser.getAvatar())
                    .setPassword(sysUser.getPassword())
                    .setAccountStatus(userStatus.isStatusBoolean());
        }
        if (ObjectUtil.isNotEmpty(userInfo)) {
            List<String> roles = getAnyRolesById(sysUser.getUserId());
            userInfo.setRoles(roles);
        }
        return userInfo;
    }

    @Override
    @RedisCachePut(cacheName = Redis.login_user, condition = "#re!=null&&#re.user!=null", key = "#re.user.id", responseAsName = "re")
    public User login(String username, String password) {
        UserInfo userInfo = getOneByUserName(username);
        if (ObjectUtil.isEmpty(userInfo)) {
            throw new GlobalCustomException("用户不存在");
        } else if (!EncodePasswordUtils.matchPassword(password, userInfo.getPassword())) {
            throw new GlobalCustomException("密码错误");
        }
        User userRe = new User(userInfo, userInfo.getRoles());
        return userRe;
    }


    /**
     * @param nickname
     * @param password
     * @param password2
     * @return
     */
    @Override
    public User register(String nickname, String password, String password2) {
        if (!ObjectUtil.equals(password, password2)) {
            throw new GlobalCustomException("两次密码不一致");
        }
        SysUser sysUser = new SysUser()
                .setUserName(String.valueOf(IdUtil.getSnowflake().nextId()))
                .setPassword(EncodePasswordUtils.encodePassword(password))
                .setNickName(nickname);
        save(sysUser);
        sysUser = getById(sysUser.getUserId());
        SysUserStatus userStatus = EnumUtils.getEnumByPrivateFieldName(SysUserStatus.class, sysUser.getStatus(), "status");

        UserInfo userInfo = new UserInfo();
        userInfo.setNickname(sysUser.getNickName())
                .setUsername(sysUser.getUserName())
                .setPassword(password)
                .setId(String.valueOf(sysUser.getUserId()))
                .setImage(sysUser.getAvatar())
                .setAccountStatus(userStatus.isStatusBoolean());
        User user = new User(userInfo, null);
        return user;
    }

    /**
     * @param id
     * @return
     */
    @Override
    @RedisCacheable(cacheName = Redis.login_user, condition = "#rq.id!=null", key = "#rq.id", requestAsName = "rq", classType = User.class)
    public User getOneRedis(String id) {
        return null;
    }

    @Override
    public List<String> getRoles(Long userId) {
        List<String> roles = SysUserService.super.getRoles(userId);
        List<Long> userIds = CollUtil.newArrayList(userId);
        List<Long> roleIds = sysUserRoleService.listByIds(userIds).stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
        List<String> roleKeys = sysRoleService.getRoleKeys(roleIds);
        roles.addAll(roleKeys);
        return roles;
    }

    @Override
    public List<String> getPerms(Long userId) {
        List<String> perms = SysUserService.super.getPerms(userId);
        List<Long> userIds = CollUtil.newArrayList(userId);
        List<Long> roleIds = sysUserRoleService.listByIds(userIds).stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
        List<String> permsKeys = sysRoleMenuService.getPermsKeys(roleIds);
        perms.addAll(permsKeys);
        return perms;
    }

    /**
     * 查询业务信息
     *
     * @param userId
     * @return 结果
     */
    @Override
    public SysUser selectSysUserByUserId(Long userId) {
        return getById(userId);
    }

    /**
     * 查询业务信息列表
     *
     * @param sysUser 用户信息
     * @return 业务信息集合
     */
    @Override
    public List<SysUser> selectSysUserList(SysUser sysUser) {
        return list();
    }

    /**
     * 删除业务信息
     *
     * @param userIds 需要删除的表数据List
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByUserIds(List<Long> userIds) {
        boolean removeByIds = removeByIds(userIds);
        return removeByIds;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatch(List<SysUser> list) {
        return baseMapper.updateBatch(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatchSelective(List<SysUser> list) {
        return baseMapper.updateBatchSelective(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsert(List<SysUser> list) {
        return baseMapper.batchInsert(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsertSelectiveUseDefaultForNull(List<SysUser> list) {
        return baseMapper.batchInsertSelectiveUseDefaultForNull(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByPrimaryKeyIn(List<Long> list) {
        return baseMapper.deleteByPrimaryKeyIn(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOrUpdate(SysUser record) {
        return baseMapper.insertOrUpdate(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOrUpdateSelective(SysUser record) {
        return baseMapper.insertOrUpdateSelective(record);
    }

    @Override
    public List<SysUser> getFriends(SysUser user, List<Long> friendIds, boolean commonDatasource) {
        return sysUserDao.getFriends(user,friendIds,commonDatasource);
    }

    @Override
    public List<SysUser> applyList(Long userId) {
        return baseMapper.applyList(userId);
    }

    @Override
    public SysUser getUser(Long chatId, Long userId, ChatType chatType) {
        return baseMapper.getUser(chatId, userId, chatType);
    }

    @Override
    public SysUser getOneUser(Long userId, Long nowUserId) {
        return baseMapper.getOneUser(userId, nowUserId);
    }

    @Override
    public List<SysUser> getUsers(SysUser user) {
        return baseMapper.getUsers(user);
    }
}

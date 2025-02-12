package com.minimalism.user.service;

import com.minimalism.abstractinterface.service.AbstractUserService;
import com.minimalism.aop.redis.RedisCachePut;
import com.minimalism.constant.Redis;
import com.minimalism.pojo.TokenInfo;
import com.minimalism.pojo.User;
import com.minimalism.pojo.UserInfo;
import com.minimalism.user.domain.SysUser;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author minimalism
 * @Date 2024/9/28 上午1:26:19
 * @Description
 */
public interface SysUserService extends IService<SysUser>, AbstractUserService {
    /**
     * @param userName
     * @return
     */
    @Override
    default UserInfo getOneByUserName(String userName) {
        return AbstractUserService.super.getOneByUserName(userName);
    }

    @RedisCachePut(cacheName = Redis.login_user, condition = "#re!=null&&#re.user!=null", key = "#re.user.id", responseAsName = "re")
    User login(String username, String password);




    @Override
    User register(String nickname, String password, String password2);

    /**
     * @param id
     * @return
     */
    @Override
    User getOneRedis(String id);

    /**
     * 查询角色权限
     *
     * @param userId
     * @return
     */
    @Override
    default List<String> getRoles(Long userId) {
        return AbstractUserService.super.getRoles(userId);
    }

    /**
     * @param userId
     * @return
     */
    default List<String> getPerms(Long userId) {
        return AbstractUserService.super.getPerms(userId);
    }

    SysUser selectSysUserByUserId(Long userId);

    List<SysUser> selectSysUserList(SysUser sysUser);

    boolean deleteByUserIds(List<Long> userIds);

    int updateBatch(List<SysUser> list);

    int updateBatchSelective(List<SysUser> list);

    int batchInsert(List<SysUser> list);

    int batchInsertSelectiveUseDefaultForNull(List<SysUser> list);

    int deleteByPrimaryKeyIn(List<Long> list);

    int insertOrUpdate(SysUser record);

    int insertOrUpdateSelective(SysUser record);


}

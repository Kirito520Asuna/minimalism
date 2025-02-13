package com.minimalism.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minimalism.enums.im.ChatType;
import com.minimalism.user.domain.SysUser;
import com.minimalism.vo.user.UserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author yan
 * @Date 2024/9/28 上午1:26:19
 * @Description
 */
@Mapper
public interface SysUserDao extends BaseMapper<SysUser> {
    int updateBatch(List<SysUser> list);

    int updateBatchSelective(List<SysUser> list);

    int batchInsert(@Param("list") List<SysUser> list);

    int batchInsertSelectiveUseDefaultForNull(@Param("list") List<SysUser> list);

    int deleteByPrimaryKeyIn(List<Long> list);

    int insertOrUpdate(SysUser record);

    int insertOrUpdateSelective(SysUser record);

    List<SysUser> getFriends(SysUser user);

    List<SysUser> applyList(@Param("userId") Long userId);

    SysUser getUser(@Param("chatId") Long chatId, @Param("userId") Long userId, @Param("chatType") ChatType chatType);

    SysUser getOneUser(@Param("userId") Long userId, @Param("nowUserId") Long nowUserId);

    List<SysUser> getUsers(SysUser user);
    UserVo getUserVoById(Long id);
}
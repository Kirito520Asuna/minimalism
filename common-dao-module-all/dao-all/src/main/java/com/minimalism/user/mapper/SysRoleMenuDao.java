package com.minimalism.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minimalism.abstractinterface.mapper.MpMapper;
import com.minimalism.user.domain.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author yan
 * @Date 2024/9/28 上午1:26:19
 * @Description
 */
@Mapper
public interface SysRoleMenuDao extends MpMapper<SysRoleMenu> {
    int updateBatch(List<SysRoleMenu> list);
    int updateBatchSelective(List<SysRoleMenu> list);
    int batchInsert(@Param("list") List<SysRoleMenu> list);
    int batchInsertSelectiveUseDefaultForNull(@Param("list") List<SysRoleMenu> list);
    int insertOrUpdate(SysRoleMenu record);
    int insertOrUpdateSelective(SysRoleMenu record);
}
package com.minimalism.user.service;

import java.util.List;

import com.minimalism.user.domain.SysRoleMenu;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Author minimalism
 * @Date 2024/9/28 上午1:26:19
 * @Description
 */
public interface SysRoleMenuService extends IService<SysRoleMenu> {


    List<String> getPermsKeys(List<Long> rolesId);

    int updateBatch(List<SysRoleMenu> list);

    int updateBatchSelective(List<SysRoleMenu> list);

    int batchInsert(List<SysRoleMenu> list);

    int batchInsertSelectiveUseDefaultForNull(List<SysRoleMenu> list);

    int insertOrUpdate(SysRoleMenu record);

    int insertOrUpdateSelective(SysRoleMenu record);

}

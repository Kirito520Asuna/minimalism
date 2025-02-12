package com.minimalism.user.service;

import com.minimalism.user.domain.SysUserRole;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
    /**
 * @Author minimalism
 * @Date 2024/9/28 上午1:26:19
 * @Description
 */
public interface SysUserRoleService extends IService<SysUserRole>{


    int updateBatch(List<SysUserRole> list);

    int updateBatchSelective(List<SysUserRole> list);

    int batchInsert(List<SysUserRole> list);

    int batchInsertSelectiveUseDefaultForNull(List<SysUserRole> list);

    int deleteByPrimaryKeyIn(List<Long> list);

    int insertOrUpdate(SysUserRole record);

    int insertOrUpdateSelective(SysUserRole record);

}

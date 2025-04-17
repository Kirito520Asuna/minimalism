package com.minimalism.dept.service;

import java.util.List;
import com.minimalism.dept.domain.SysDept;
import com.baomidou.mybatisplus.extension.service.IService;
public interface SysDeptService extends IService<SysDept>{


    int updateBatch(List<SysDept> list);

    int updateBatchUseMultiQuery(List<SysDept> list);

    int updateBatchSelective(List<SysDept> list);

    int batchInsert(List<SysDept> list);

    int batchInsertSelectiveUseDefaultForNull(List<SysDept> list);

    int insertOrUpdate(SysDept record);

    int insertOrUpdateSelective(SysDept record);

    /**
     * 查询部门管理数据
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    List<SysDept> selectDeptList(SysDept dept);

    /**
     * 校验部门是否有数据权限
     *
     * @param deptId 部门id
     */
    void checkDeptDataScope(Long deptId);
}

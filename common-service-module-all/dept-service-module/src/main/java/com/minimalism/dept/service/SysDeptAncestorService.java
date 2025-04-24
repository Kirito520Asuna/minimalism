package com.minimalism.dept.service;

import java.util.List;
import com.minimalism.dept.domain.SysDeptAncestor;
import com.baomidou.mybatisplus.extension.service.IService;
public interface SysDeptAncestorService extends IService<SysDeptAncestor>{


    int updateBatch(List<SysDeptAncestor> list);

    int updateBatchUseMultiQuery(List<SysDeptAncestor> list);

    int updateBatchSelective(List<SysDeptAncestor> list);

    int batchInsert(List<SysDeptAncestor> list);

    int batchInsertSelectiveUseDefaultForNull(List<SysDeptAncestor> list);

    int insertOrUpdate(SysDeptAncestor record);

    int insertOrUpdateSelective(SysDeptAncestor record);

    List<SysDeptAncestor> selectDeptAncestorList(SysDeptAncestor deptAncestor);

    List<SysDeptAncestor> selectDeptAncestorListByAncestorDeptId(Long deptId);

    /**
     * 查询所有子部门id
     * @param deptId
     * @return
     */
    List<Long> selectSubDeptAncestorListByAncestorDeptParentId(Long deptId);
}

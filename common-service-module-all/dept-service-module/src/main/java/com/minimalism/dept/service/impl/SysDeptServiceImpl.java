package com.minimalism.dept.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import com.minimalism.dept.mapper.SysDeptMapper;
import com.minimalism.dept.domain.SysDept;
import com.minimalism.dept.service.SysDeptService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService{

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatch(List<SysDept> list) {
        return baseMapper.updateBatch(list);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatchUseMultiQuery(List<SysDept> list) {
        return baseMapper.updateBatchUseMultiQuery(list);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatchSelective(List<SysDept> list) {
        return baseMapper.updateBatchSelective(list);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsert(List<SysDept> list) {
        return baseMapper.batchInsert(list);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsertSelectiveUseDefaultForNull(List<SysDept> list) {
        return baseMapper.batchInsertSelectiveUseDefaultForNull(list);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOrUpdate(SysDept record) {
        return baseMapper.insertOrUpdate(record);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOrUpdateSelective(SysDept record) {
        return baseMapper.insertOrUpdateSelective(record);
    }

    @Override
    public List<SysDept> selectDeptList(SysDept dept) {
        return baseMapper.selectSysDeptList(dept);
    }

    @Override
    public void checkDeptDataScope(Long deptId) {

    }
}

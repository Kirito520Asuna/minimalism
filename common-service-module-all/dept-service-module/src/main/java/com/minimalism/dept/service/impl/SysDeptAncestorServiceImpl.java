package com.minimalism.dept.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

import com.minimalism.dept.mapper.SysDeptAncestorMapper;
import com.minimalism.dept.domain.SysDeptAncestor;
import com.minimalism.dept.service.SysDeptAncestorService;

@Service
public class SysDeptAncestorServiceImpl extends ServiceImpl<SysDeptAncestorMapper, SysDeptAncestor> implements SysDeptAncestorService {

    @Override
    public int updateBatch(List<SysDeptAncestor> list) {
        return baseMapper.updateBatch(list);
    }

    @Override
    public int updateBatchUseMultiQuery(List<SysDeptAncestor> list) {
        return baseMapper.updateBatchUseMultiQuery(list);
    }

    @Override
    public int updateBatchSelective(List<SysDeptAncestor> list) {
        return baseMapper.updateBatchSelective(list);
    }

    @Override
    public int batchInsert(List<SysDeptAncestor> list) {
        return baseMapper.batchInsert(list);
    }

    @Override
    public int batchInsertSelectiveUseDefaultForNull(List<SysDeptAncestor> list) {
        return baseMapper.batchInsertSelectiveUseDefaultForNull(list);
    }

    @Override
    public int insertOrUpdate(SysDeptAncestor record) {
        return baseMapper.insertOrUpdate(record);
    }

    @Override
    public int insertOrUpdateSelective(SysDeptAncestor record) {
        return baseMapper.insertOrUpdateSelective(record);
    }

    @Override
    public List<SysDeptAncestor> selectDeptAncestorList(SysDeptAncestor deptAncestor) {
        Long id = deptAncestor.getId();
        Long deptId = deptAncestor.getDeptId();
        Long deptParentId = deptAncestor.getDeptParentId();
        Long level = deptAncestor.getLevel();
        LambdaQueryWrapper<SysDeptAncestor> wrapper = Wrappers.lambdaQuery(SysDeptAncestor.class);
        wrapper.eq(id != null, SysDeptAncestor::getId, id)
                .eq(deptId != null, SysDeptAncestor::getDeptId, deptId)
                .eq(deptParentId != null, SysDeptAncestor::getDeptParentId, deptParentId)
                .eq(level != null, SysDeptAncestor::getLevel, level);

        return list(wrapper);
    }

    @Override
    public List<SysDeptAncestor> selectDeptAncestorListByAncestorDeptId(Long deptId) {
        LambdaQueryWrapper<SysDeptAncestor> query = Wrappers.lambdaQuery(SysDeptAncestor.class);
        query.eq(SysDeptAncestor::getDeptParentId, deptId);
        return list(query);
    }

    @Override
    public List<Long> selectSubDeptAncestorListByAncestorDeptParentId(Long deptId) {
        LambdaQueryWrapper<SysDeptAncestor> query = new QueryWrapper<SysDeptAncestor>()
                .select("DISTINCT " + SysDeptAncestor.COL_DEPT_ID)
                .lambda();
        query.eq(SysDeptAncestor::getDeptParentId, deptId)
                .ge(SysDeptAncestor::getLevel, 0);
        List<Long> collect = list(query).stream().map(SysDeptAncestor::getDeptId).collect(Collectors.toList());
        return collect;
    }
}

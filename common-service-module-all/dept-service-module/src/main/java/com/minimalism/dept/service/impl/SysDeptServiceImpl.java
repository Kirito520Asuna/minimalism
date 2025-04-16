package com.minimalism.dept.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import com.minimalism.dept.mapper.SysDeptMapper;
import com.minimalism.dept.domain.SysDept;
import com.minimalism.dept.service.SysDeptService;
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService{

    @Override
    public int updateBatch(List<SysDept> list) {
        return baseMapper.updateBatch(list);
    }
    @Override
    public int updateBatchUseMultiQuery(List<SysDept> list) {
        return baseMapper.updateBatchUseMultiQuery(list);
    }
    @Override
    public int updateBatchSelective(List<SysDept> list) {
        return baseMapper.updateBatchSelective(list);
    }
    @Override
    public int batchInsert(List<SysDept> list) {
        return baseMapper.batchInsert(list);
    }
    @Override
    public int batchInsertSelectiveUseDefaultForNull(List<SysDept> list) {
        return baseMapper.batchInsertSelectiveUseDefaultForNull(list);
    }
    @Override
    public int insertOrUpdate(SysDept record) {
        return baseMapper.insertOrUpdate(record);
    }
    @Override
    public int insertOrUpdateSelective(SysDept record) {
        return baseMapper.insertOrUpdateSelective(record);
    }
}

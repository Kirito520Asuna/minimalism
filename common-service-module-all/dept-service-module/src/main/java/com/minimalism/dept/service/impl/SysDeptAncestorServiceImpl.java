package com.minimalism.dept.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import com.minimalism.dept.mapper.SysDeptAncestorMapper;
import com.minimalism.dept.domain.SysDeptAncestor;
import com.minimalism.dept.service.SysDeptAncestorService;
@Service
public class SysDeptAncestorServiceImpl extends ServiceImpl<SysDeptAncestorMapper, SysDeptAncestor> implements SysDeptAncestorService{

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
}

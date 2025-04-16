package com.minimalism.dict.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import com.minimalism.dict.mapper.SysDictDataMapper;
import com.minimalism.dict.domain.SysDictData;
import com.minimalism.dict.service.SysDictDataService;
@Service
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements SysDictDataService{

    @Override
    public int updateBatch(List<SysDictData> list) {
        return baseMapper.updateBatch(list);
    }
    @Override
    public int updateBatchUseMultiQuery(List<SysDictData> list) {
        return baseMapper.updateBatchUseMultiQuery(list);
    }
    @Override
    public int updateBatchSelective(List<SysDictData> list) {
        return baseMapper.updateBatchSelective(list);
    }
    @Override
    public int batchInsert(List<SysDictData> list) {
        return baseMapper.batchInsert(list);
    }
    @Override
    public int batchInsertSelectiveUseDefaultForNull(List<SysDictData> list) {
        return baseMapper.batchInsertSelectiveUseDefaultForNull(list);
    }
    @Override
    public int insertOnDuplicateUpdate(SysDictData record) {
        return baseMapper.insertOnDuplicateUpdate(record);
    }
    @Override
    public int insertOnDuplicateUpdateSelective(SysDictData record) {
        return baseMapper.insertOnDuplicateUpdateSelective(record);
    }
}

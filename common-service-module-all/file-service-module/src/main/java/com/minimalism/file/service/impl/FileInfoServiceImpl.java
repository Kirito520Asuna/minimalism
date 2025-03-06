package com.minimalism.file.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minimalism.file.domain.FileInfo;
import com.minimalism.file.mapper.FileInfoMapper;
import com.minimalism.file.service.FileInfoService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author yan
 * @Date 2025/3/6 14:16:20
 * @Description
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements FileInfoService{

    @Override
    public int updateBatch(List<FileInfo> list) {
        return baseMapper.updateBatch(list);
    }
    @Override
    public int updateBatchUseMultiQuery(List<FileInfo> list) {
        return baseMapper.updateBatchUseMultiQuery(list);
    }
    @Override
    public int updateBatchSelective(List<FileInfo> list) {
        return baseMapper.updateBatchSelective(list);
    }
    @Override
    public int batchInsert(List<FileInfo> list) {
        return baseMapper.batchInsert(list);
    }
    @Override
    public int batchInsertSelectiveUseDefaultForNull(List<FileInfo> list) {
        return baseMapper.batchInsertSelectiveUseDefaultForNull(list);
    }
    @Override
    public int batchInsertOrUpdate(List<FileInfo> list) {
        return baseMapper.batchInsertOrUpdate(list);
    }
    @Override
    public int deleteByPrimaryKeyIn(List<Long> list) {
        return baseMapper.deleteByPrimaryKeyIn(list);
    }
    @Override
    public int insertOnDuplicateUpdate(FileInfo record) {
        return baseMapper.insertOnDuplicateUpdate(record);
    }
    @Override
    public int insertOnDuplicateUpdateSelective(FileInfo record) {
        return baseMapper.insertOnDuplicateUpdateSelective(record);
    }
}

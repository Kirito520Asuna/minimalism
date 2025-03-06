package com.minimalism.file.service.impl;

import com.minimalism.file.service.FilePartService;
import org.springframework.stereotype.Service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minimalism.file.mapper.FilePartMapper;
import com.minimalism.file.domain.FilePart;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author yan
 * @Date 2025/3/6 14:16:20
 * @Description
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FilePartServiceImpl extends ServiceImpl<FilePartMapper, FilePart> implements FilePartService {

    @Override
    public int updateBatch(List<FilePart> list) {
        return baseMapper.updateBatch(list);
    }
    @Override
    public int updateBatchUseMultiQuery(List<FilePart> list) {
        return baseMapper.updateBatchUseMultiQuery(list);
    }
    @Override
    public int updateBatchSelective(List<FilePart> list) {
        return baseMapper.updateBatchSelective(list);
    }
    @Override
    public int batchInsert(List<FilePart> list) {
        return baseMapper.batchInsert(list);
    }
    @Override
    public int batchInsertSelectiveUseDefaultForNull(List<FilePart> list) {
        return baseMapper.batchInsertSelectiveUseDefaultForNull(list);
    }
    @Override
    public int batchInsertOrUpdate(List<FilePart> list) {
        return baseMapper.batchInsertOrUpdate(list);
    }
    @Override
    public int deleteByPrimaryKeyIn(List<Long> list) {
        return baseMapper.deleteByPrimaryKeyIn(list);
    }
    @Override
    public int insertOnDuplicateUpdate(FilePart record) {
        return baseMapper.insertOnDuplicateUpdate(record);
    }
    @Override
    public int insertOnDuplicateUpdateSelective(FilePart record) {
        return baseMapper.insertOnDuplicateUpdateSelective(record);
    }
}

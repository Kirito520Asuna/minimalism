package com.minimalism.file.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.minimalism.file.domain.FileInfo;
import com.minimalism.file.service.FilePartService;
import com.minimalism.vo.PartVo;
import org.springframework.stereotype.Service;

import java.util.Collections;
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

    @Override
    public List<PartVo> getPartList(String identifier, Long fileId) {
        return baseMapper.getPartList(identifier, fileId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int removePart(String identifier, Long fileId) {
        List<PartVo> partList = getPartList(identifier, fileId);
        List<PartVo> localList = CollUtil.newArrayList();
        List<PartVo> urlList = CollUtil.newArrayList();
        if (CollUtil.isNotEmpty(partList)) {
            partList.stream().forEach(part -> {
                if (part.getLocal()) {
                    localList.add(part);
                } else {
                    urlList.add(part);
                }
            });
        }
        //删除云端文件
        // todo:

        //删除本地文件
        localList.stream().map(PartVo::getLocalResource).forEach(FileUtil::del);

        //删除数据库记录
        return baseMapper.deleteByFileId(identifier, fileId);
    }

}

package com.minimalism.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minimalism.file.domain.FileInfo;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author yan
 * @Date 2025/3/6 15:28:04
 * @Description
 */
@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfo> {
    int updateBatch(@Param("list") List<FileInfo> list);

    int updateBatchUseMultiQuery(@Param("list") List<FileInfo> list);

    int updateBatchSelective(@Param("list") List<FileInfo> list);

    int batchInsert(@Param("list") List<FileInfo> list);

    int batchInsertSelectiveUseDefaultForNull(@Param("list") List<FileInfo> list);

    int batchInsertOrUpdate(@Param("list") List<FileInfo> list);

    int deleteByPrimaryKeyIn(List<Long> list);

    int insertOnDuplicateUpdate(FileInfo record);

    int insertOnDuplicateUpdateSelective(FileInfo record);
}
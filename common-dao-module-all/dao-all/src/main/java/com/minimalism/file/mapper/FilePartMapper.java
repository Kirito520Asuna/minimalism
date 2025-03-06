package com.minimalism.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minimalism.file.domain.FilePart;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author yan
 * @Date 2025/3/6 14:19:22
 * @Description
 */
@Mapper
public interface FilePartMapper extends BaseMapper<FilePart> {
    int updateBatch(@Param("list") List<FilePart> list);

    int updateBatchUseMultiQuery(@Param("list") List<FilePart> list);

    int updateBatchSelective(@Param("list") List<FilePart> list);

    int batchInsert(@Param("list") List<FilePart> list);

    int batchInsertSelectiveUseDefaultForNull(@Param("list") List<FilePart> list);

    int batchInsertOrUpdate(@Param("list") List<FilePart> list);

    int deleteByPrimaryKeyIn(List<Long> list);

    int insertOnDuplicateUpdate(FilePart record);

    int insertOnDuplicateUpdateSelective(FilePart record);

}
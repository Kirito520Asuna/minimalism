package com.minimalism.dict.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minimalism.dict.domain.SysDictType;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysDictTypeMapper extends BaseMapper<SysDictType> {
    int updateBatch(@Param("list") List<SysDictType> list);

    int updateBatchUseMultiQuery(@Param("list") List<SysDictType> list);

    int updateBatchSelective(@Param("list") List<SysDictType> list);

    int batchInsert(@Param("list") List<SysDictType> list);

    int batchInsertSelectiveUseDefaultForNull(@Param("list") List<SysDictType> list);

    int insertOnDuplicateUpdate(SysDictType record);

    int insertOnDuplicateUpdateSelective(SysDictType record);
}
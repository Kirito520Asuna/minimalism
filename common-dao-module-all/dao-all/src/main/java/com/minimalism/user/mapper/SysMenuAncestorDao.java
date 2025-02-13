package com.minimalism.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minimalism.user.domain.SysMenuAncestor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author yan
 * @Date 2024/9/28 上午1:26:20
 * @Description
 */
@Mapper
public interface SysMenuAncestorDao extends BaseMapper<SysMenuAncestor> {
    int updateBatch(@Param("list") List<SysMenuAncestor> list);

    int updateBatchSelective(@Param("list") List<SysMenuAncestor> list);

    int batchInsert(@Param("list") List<SysMenuAncestor> list);

    int batchInsertSelectiveUseDefaultForNull(@Param("list") List<SysMenuAncestor> list);

    int deleteByPrimaryKeyIn(@Param("list") List<Long> list);

    int insertOrUpdate(SysMenuAncestor record);

    int insertOrUpdateSelective(SysMenuAncestor record);

    List<SysMenuAncestor> selectByCustomMap(Map<String, Object> hashMap);

    int deleteByCustomMap(@Param("map") Map<String, Object> deleteMap);
}
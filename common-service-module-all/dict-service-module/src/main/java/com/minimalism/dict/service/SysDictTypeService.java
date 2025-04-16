package com.minimalism.dict.service;

import java.util.List;
import com.minimalism.dict.domain.SysDictType;
import com.baomidou.mybatisplus.extension.service.IService;
public interface SysDictTypeService extends IService<SysDictType>{


    int updateBatch(List<SysDictType> list);

    int updateBatchUseMultiQuery(List<SysDictType> list);

    int updateBatchSelective(List<SysDictType> list);

    int batchInsert(List<SysDictType> list);

    int batchInsertSelectiveUseDefaultForNull(List<SysDictType> list);

    int insertOnDuplicateUpdate(SysDictType record);

    int insertOnDuplicateUpdateSelective(SysDictType record);

}

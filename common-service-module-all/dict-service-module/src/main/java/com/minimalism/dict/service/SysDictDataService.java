package com.minimalism.dict.service;

import java.util.List;
import com.minimalism.dict.domain.SysDictData;
import com.baomidou.mybatisplus.extension.service.IService;
public interface SysDictDataService extends IService<SysDictData>{


    int updateBatch(List<SysDictData> list);

    int updateBatchUseMultiQuery(List<SysDictData> list);

    int updateBatchSelective(List<SysDictData> list);

    int batchInsert(List<SysDictData> list);

    int batchInsertSelectiveUseDefaultForNull(List<SysDictData> list);

    int insertOnDuplicateUpdate(SysDictData record);

    int insertOnDuplicateUpdateSelective(SysDictData record);

}

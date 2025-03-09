package com.minimalism.file.service;

import java.util.List;

import com.minimalism.file.domain.FilePart;
import com.baomidou.mybatisplus.extension.service.IService;
import com.minimalism.vo.PartVo;

/**
 * @Author yan
 * @Date 2025/3/6 14:16:20
 * @Description
 */
public interface FilePartService extends IService<FilePart> {


    int updateBatch(List<FilePart> list);

    int updateBatchUseMultiQuery(List<FilePart> list);

    int updateBatchSelective(List<FilePart> list);

    int batchInsert(List<FilePart> list);

    int batchInsertSelectiveUseDefaultForNull(List<FilePart> list);

    int batchInsertOrUpdate(List<FilePart> list);

    int deleteByPrimaryKeyIn(List<Long> list);

    int insertOnDuplicateUpdate(FilePart record);

    int insertOnDuplicateUpdateSelective(FilePart record);

    List<PartVo> getPartList(String identifier, Long fileId);

    int removePart(String identifier, Long fileId);

    Long getOneFileIdByCode(String identifier);

    boolean updateByEntityFileId(FilePart filePart);

    int getCountByFileId(Long fileId, String identifier);

    List<FilePart> getPartsByFileIdFirstPartCount(Long fileId, int partCount,  List<Long> excludes);
}

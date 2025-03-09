package com.minimalism.file.service;

import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.vo.PartVo;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

/**
 * @Author yan
 * @Date 2025/3/6 15:15:12
 * @Description
 */
public interface FileService extends AbstractBean {
    List<PartVo> getPartList(String identifier, Long fileId);
    List<InputStream> getPartInputStreamList(String identifier, Long fileId);
    InputStream partToInputStream(PartVo partVo);
    ByteArrayOutputStream mergeOutputStream(String identifier, Long fileId);
    boolean mergeOk(String identifier, Long fileId);
    String getPartPath(String identifier, Integer chunkNumber);

    String getMergePartPath(String identifier, String fileName, String suffix);

    boolean uploadChunk(InputStream inputStream, String identifier, int chunkNumber, int totalChunks, Long fileId);

    boolean mergeChunks(String identifier, Long fileId, String fileName);

    boolean uploadMergeChunks(String identifier, int totalChunks, String fileName);
}

package com.minimalism.file.service;

import cn.hutool.core.io.FileUtil;
import com.minimalism.exception.GlobalCustomException;
import com.minimalism.utils.object.ObjectUtils;
import com.minimalism.vo.PartVo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2025/3/6 15:15:12
 * @Description
 */
public interface FileService {
    List<PartVo> getPartList(String identifier, Long fileId);
    List<InputStream> getPartInputStreamList(String identifier, Long fileId);
    InputStream partToInputStream(PartVo partVo);
    ByteArrayOutputStream mergeOutputStream(String identifier, Long fileId);
    boolean mergeOk(String identifier, Long fileId);
    String getPartPath(String identifier, Integer chunkNumber);

    String getMergePartPath(String identifier, String fileName, String suffix);

    boolean uploadChunk(String path, InputStream inputStream, String identifier, int chunkNumber, int totalChunks, Long fileId);

    boolean mergeChunks(String identifier, Long fileId);
}

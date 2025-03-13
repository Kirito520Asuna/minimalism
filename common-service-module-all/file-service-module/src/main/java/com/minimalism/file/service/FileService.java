package com.minimalism.file.service;

import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.vo.PartVo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @Author yan
 * @Date 2025/3/6 15:15:12
 * @Description
 */
public interface FileService extends AbstractBean {
    PartVo partToInputStream(PartVo partVo);

    List<PartVo> getPartList(String identifier, Long fileId);

    List<InputStream> getPartInputStreamList(String identifier, Long fileId);


    String getPartPath(String identifier, Integer chunkNumber);

    String getMergePartPath(String identifier, String fileName, String suffix);

    /*###################################云端上传#####################################################################################################################################################################################*/
    boolean uploadChunk(InputStream inputStream, String identifier, int chunkNumber, int totalChunks, Long fileId);

    boolean uploadMergeChunks(String identifier, int totalChunks, String fileName);


    /*###################################本机合并#####################################################################################################################################################################################*/
    boolean mergeChunks(String identifier, Long fileId, String fileName);

    void mergeMore(Long fileId, String identifier) throws IOException;

    ByteArrayOutputStream mergeOutputStream(String identifier, Long fileId);

    boolean mergeOk(String identifier, Long fileId);

    /**
     * 获取文件字节
     * uploadDir + folder +/+ identifier +/+ chunkNumber.part
     * or
     * uploadDir + folder +/+ identifier
     * or
     * uploadDir + fileName(优先)
     *
     * @param fileName    文件
     * @param folder      文件夹
     * @param identifier  文件唯一标识
     * @param chunkNumber 分块编号
     * @return
     */
    List<byte[]> getByteByLocal(String fileName, String folder, String identifier, Integer chunkNumber);

    /**
     * uploadDir + folder +/+ identifier +/+ chunkNumber.part
     * or
     * uploadDir + folder +/+ identifier
     * or
     * uploadDir + fileName(优先)
     *
     * @param fileName    文件
     * @param folder      文件夹
     * @param identifier  文件唯一标识
     * @param chunkNumber 分块编号
     * @return
     */
    boolean delByteByLocal(String fileName, String folder, String identifier, Integer chunkNumber);
}

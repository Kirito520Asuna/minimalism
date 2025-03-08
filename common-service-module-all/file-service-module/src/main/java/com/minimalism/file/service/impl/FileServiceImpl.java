package com.minimalism.file.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.exception.GlobalCustomException;
import com.minimalism.file.domain.FileInfo;
import com.minimalism.file.domain.FilePart;
import com.minimalism.file.properties.FileProperties;
import com.minimalism.file.service.FileInfoService;
import com.minimalism.file.service.FilePartService;
import com.minimalism.file.service.FileService;
import com.minimalism.file.storage.FileFactory;
import com.minimalism.file.storage.IFileStorageClient;
import com.minimalism.file.storage.StorageType;
import com.minimalism.utils.io.IoUtils;
import com.minimalism.utils.object.ObjectUtils;
import com.minimalism.vo.PartVo;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2025/3/6 15:15:24
 * @Description
 */
@Service
public class FileServiceImpl implements FileService {

    @Resource
    private FilePartService filePartService;
    @Resource
    private FileInfoService fileInfoService;

    @Override
    public List<PartVo> getPartList(String identifier, Long fileId) {
        return filePartService.getPartList(identifier, fileId);
    }

    @Override
    public InputStream partToInputStream(PartVo partVo) {
        InputStream inputStream = null;
        Boolean local = partVo.getLocal();
        if (ObjectUtils.defaultIfEmpty(local, Boolean.FALSE)) {
            inputStream = FileUtil.getInputStream(partVo.getLocalResource());
        } else {
            try {
                inputStream = new URL(partVo.getUrl()).openStream();
            } catch (IOException e) {
                throw new GlobalCustomException(e.getMessage());
            }
        }
        return inputStream;
    }

    @Override
    public List<InputStream> getPartInputStreamList(String identifier, Long fileId) {
        List<PartVo> partList = getPartList(identifier, fileId);
        List<InputStream> inputStreamList = partList.stream().map(this::partToInputStream).collect(Collectors.toList());
        return inputStreamList;
    }

    @Override
    public ByteArrayOutputStream mergeOutputStream(String identifier, Long fileId) {
        List<InputStream> list = getPartInputStreamList(identifier, fileId);
        ByteArrayOutputStream out = IoUtils.merge(list);
        return out;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean mergeOk(String identifier, Long fileId) {
        filePartService.removePart(identifier, fileId);
        return false;
    }

    @Override
    public String getPartPath(String identifier, Integer chunkNumber) {
        // 使用HuTool保存分片文件
        String part = ".part";
        // 使用HuTool保存分片文件
        return FileFactory.getClient(StorageType.local).getChunkDirPath(identifier) + chunkNumber + part;
    }

    @Override
    public String getMergePartPath(String identifier, String fileName, String suffix) {
        // 使用HuTool保存分片文件
        return FileFactory.getClient(StorageType.local).getMergeFilePath(fileName + suffix);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean uploadChunk(InputStream inputStream, String identifier, int chunkNumber, int totalChunks, Long fileId) {
        IFileStorageClient client = FileFactory.getClient(StorageType.local);
        FilePart filePart = client.uploadShardingChunkNumber(chunkNumber, identifier, inputStream).setFileId(fileId);
        filePartService.save(filePart);
        return true;
    }

    @SneakyThrows
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean mergeChunks(String identifier, Long fileId, String fileName) {
        ByteArrayOutputStream outputStream = mergeOutputStream(identifier, fileId);
        if (fileId == null) {
            fileId = filePartService.getOneFileIdByCode(identifier);
        }

        String mainName;
        String suffix;
        String fileMainName;
        String path;

        if (fileId != null) {
            FileInfo fileInfo = fileInfoService.getById(fileId);
            mainName = FileUtil.mainName(fileInfo.getFileName());
            suffix = fileInfo.getSuffix();
            path = getMergePartPath(identifier, "tmp_" + mainName, suffix);
        } else {
            mainName = FileUtil.mainName(fileName);
            suffix = FileUtil.getSuffix(fileName);
            path = getMergePartPath(identifier, mainName, "." + FileUtil.getSuffix(fileName));
        }

        if (!suffix.startsWith(".")) {
            suffix = "." + suffix;
        }

        fileMainName = mainName + suffix;
        //生成临时文件
        File tmpFile = FileUtil.newFile(path);
        if (!tmpFile.exists()) {
            tmpFile.createNewFile();
        }

        try {
            InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            OutputStream fileOutputStream = FileUtil.getOutputStream(tmpFile);
            IoUtils.copy(inputStream, fileOutputStream);
            inputStream = FileUtil.getInputStream(path);
            FileInfo fileInfo = uploadMergeChunks(inputStream, fileMainName, identifier);
            if (fileId != null) {
                fileInfoService.updateById(fileInfo.setFileId(fileId));
            }
            mergeOk(identifier, fileId);
        } finally {
            FileUtil.del(path);
        }
        return true;
    }

    public static void main(String[] args) {
        System.err.println(FileUtil.mainName("【不忘初心】Windows10_21H2_19044.1586_X64_可更新[纯净精简版][2.53G](2022.3(1).9).zip"));
    }

    public FileInfo uploadMergeChunks(InputStream inputStream, String fileMainName, String identifier) {
        IFileStorageClient client = FileFactory.getClient(StorageType.local);
        FileInfo fileInfo = client.uploadMergeChunks(inputStream, fileMainName, identifier);
        return fileInfo.setLocal(Boolean.TRUE).setName(FileUtil.mainName(fileInfo.getFileName()));
    }

    @Override
    public boolean uploadMergeChunks(String identifier, int totalChunks, String fileName) {
        IFileStorageClient client = SpringUtil.getBean(FileFactory.class).getClient(StorageType.local);
        List<InputStream> inputStreams = client.getInputStreams(identifier, totalChunks);
        //client.

        return false;
    }


}

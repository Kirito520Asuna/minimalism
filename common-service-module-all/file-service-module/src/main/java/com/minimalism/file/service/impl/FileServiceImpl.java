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
import com.minimalism.file.storage.IFileStorageProvider;
import com.minimalism.file.storage.StorageType;
import com.minimalism.utils.object.ObjectUtils;
import com.minimalism.vo.PartVo;
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
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ByteArrayOutputStream outputStream = out) {
            for (InputStream chunk : list) {
                try (InputStream inputStream = chunk) {
                    IoUtil.copy(inputStream, outputStream);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new GlobalCustomException("合并错误！");
        }
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
        FileProperties.LocalProperties local = SpringUtil.getBean(FileProperties.class).getLocal();
        String uploadDir = ObjectUtils.defaultIfEmpty(local.getUploadDir(), "tmp/uploads/");
        // 使用HuTool保存分片文件
        String part = ".part";
        // 使用HuTool保存分片文件
        String path = uploadDir + identifier + "/" + chunkNumber + part;
        return path;
    }

    @Override
    public String getMergePartPath(String identifier, String fileName, String suffix) {
        FileProperties.LocalProperties local = SpringUtil.getBean(FileProperties.class).getLocal();
        String uploadDir = ObjectUtils.defaultIfEmpty(local.getUploadDir(), "tmp/uploads/");
        // 使用HuTool保存分片文件
        String path = uploadDir + identifier + "/" + fileName + suffix;
        return path;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean uploadChunk(String path, InputStream inputStream, String identifier, int chunkNumber, int totalChunks, Long fileId) {
        IFileStorageClient client = FileFactory.getClient(StorageType.local);
        FilePart filePart = client.uploadShardingChunkNumber(chunkNumber, identifier, inputStream).setFileId(fileId);
        filePartService.save(filePart);
        return true;
    }

    @Override
    public boolean mergeChunks(String identifier, Long fileId) {
        ByteArrayOutputStream outputStream = mergeOutputStream(identifier, fileId);
        if (fileId == null) {
            fileId = filePartService.getOneFileIdByCode(identifier);
        }

        if (fileId != null) {
            FileInfo fileInfo = fileInfoService.getById(fileId);

            String fileName = FileUtil.mainName(fileInfo.getFileName());
            String suffix = fileInfo.getSuffix();
            String fileMainName = fileName + suffix;
            String path = getMergePartPath(identifier, fileName, suffix);

            try {
                InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                OutputStream fileOutputStream = FileUtil.getOutputStream(FileUtil.newFile(path));
                IoUtil.copy(inputStream, fileOutputStream);
                inputStream = FileUtil.getInputStream(path);
                uploadMergeChunks(inputStream, fileMainName);
            } finally {
                FileUtil.del(path);
            }
        } else {
            throw new GlobalCustomException("文件不存在！");
        }
        mergeOk(identifier, fileId);
        return true;
    }

    public void uploadMergeChunks(InputStream inputStream, String fileMainName) {
        SpringUtil.getBean(IFileStorageProvider.class).getStorage()
                .uploadSharding("file", fileMainName, inputStream);
    }

}

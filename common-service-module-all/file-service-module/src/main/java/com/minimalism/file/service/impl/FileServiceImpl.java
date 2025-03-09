package com.minimalism.file.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.minimalism.constant.Constants;
import com.minimalism.enums.OSType;
import com.minimalism.exception.GlobalCustomException;
import com.minimalism.file.domain.FileInfo;
import com.minimalism.file.domain.FilePart;
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
        String part = Constants.PART_SUFFIX;
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
            long fileInfoSize = fileInfo.getSize();
            /**
             * 因为分片大小是1MB的 总大小 1GB 也就是说 超出1024个InputStream 会出现oom
             */
            long maxSize = 1024 * 1024 * 1024;
            if (fileInfoSize > maxSize) {
                if (false) {
                    //方案一
                    FilePart filePart = new FilePart()
                            .setFileId(fileId)
                            .setPartCode(identifier)
                            .setMergeDelete(Boolean.TRUE);
                    //先改需要删除的状态 然后定时任务去删除文件 和数据库中数据
                    filePartService.updateByEntityFileId(filePart);
                    fileInfoService.removeById(fileId);

                    throw new GlobalCustomException("文件过大，暂不支持大文件上传！");
                } else {
                    //方案二
                    //将分片合并 到不会oom的程度
                    mergeMore(fileId, identifier);
                }
            }
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
            ByteArrayOutputStream outputStream = mergeOutputStream(identifier, fileId);
            InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            OutputStream fileOutputStream = FileUtil.getOutputStream(tmpFile);
            IoUtils.copy(inputStream, fileOutputStream);
            inputStream = FileUtil.getInputStream(path);
            FileInfo fileInfo = uploadMergeChunks(inputStream, fileMainName, identifier);
            if (fileId != null) {
                FileInfo fileInfoById = fileInfoService.getById(fileId);
                fileInfoById.setUrl(fileInfo.getUrl())
                        .setLocal(Boolean.TRUE);
                fileInfoService.updateById(fileInfoById);
            }
            mergeOk(identifier, fileId);
        } finally {
            FileUtil.del(path);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void mergeMore(Long fileId, String identifier) throws IOException {
        int count = filePartService.getCountByFileId(fileId, identifier);
        if (count == 0) {
            throw new GlobalCustomException("[FilePart]-[mergeMore]" + "分片数量为0");
        }

        //如果分片数量大于100个 则将分片数量减少到100个
        int maxPartSize = 100;
        if (count < maxPartSize) {
            return;
        }
        //每次合并的分片数量
        int mergePartSize = 10;
        int partCount = count % mergePartSize == 0 ? (count / mergePartSize) : (count / mergePartSize) + 1;

        List<Long> excludePartIds = CollUtil.newArrayList();
        for (int i = 1; i <= partCount; i++) {
            List<FilePart> parts = filePartService.getPartsByFileIdFirstPartCount(fileId, partCount,
                    excludePartIds
            );
            //合并 todo：
            ByteArrayOutputStream outputStream = IoUtils.merge(parts.stream().map(part -> {
                InputStream inputStream;
                if (part.getLocal()) {
                    inputStream = FileUtil.getInputStream(part.getLocalResource());
                } else {
                    try {
                        inputStream = new URL(part.getUrl()).openStream();
                    } catch (IOException e) {
                        throw new GlobalCustomException("[FilePart]-[URL]-[mergeChunks]" + e.getMessage());
                    }
                }
                return inputStream;
            }).collect(Collectors.toList()));

            //合并完成
            parts.stream().forEach(part -> {
                filePartService.removeById(part.getPartId());
                if (part.getLocal()) {
                    FileUtil.del(part.getLocalResource());
                }
            });

            FilePart filePart = parts.stream().findFirst().get();
            info("{}" + Constants.PART_SUFFIX + "~{}" + Constants.PART_SUFFIX + ",合并完成",
                    FileUtil.mainName(filePart.getLocalResource()),
                    FileUtil.mainName(parts.get(parts.size() - 1).getLocalResource()));

            if (filePart.getLocal()) {
                String partFileName = i + Constants.PART_SUFFIX;
                String localResource = filePart.getLocalResource();
                // 使用字符串操作获取父目录路径

                // 只支持win,mac,linux版本
                int lastIndexOf = localResource.lastIndexOf(OSType.getSeparator(filePart.getOsType()));
                String parentPathByString = localResource.substring(0, lastIndexOf + 1);
                String pathAll = parentPathByString + partFileName;
                File file = FileUtil.newFile(pathAll);
                if (!file.exists()) {
                    file.createNewFile();
                }

                IoUtils.copy(new ByteArrayInputStream(outputStream.toByteArray()), FileUtil.getOutputStream(file));
                long size = IoUtils.size(FileUtil.getInputStream(pathAll));

                filePart.setPartSize(size);
                filePartService.updateById(filePart);
                excludePartIds.add(filePart.getPartId());
            }
        }

        if (partCount > mergePartSize) {
            //应该递归 todo：
            mergeMore(fileId, identifier);
            //临时抛出异常处理
            //throw new GlobalCustomException("文件过大，暂不支持大文件上传！");
        }
    }

    public FileInfo uploadMergeChunks(InputStream inputStream, String fileMainName, String identifier) {
        IFileStorageClient client = FileFactory.getClient(StorageType.local);
        FileInfo fileInfo = client.uploadMergeChunks(inputStream, fileMainName, identifier);
        return fileInfo.setLocal(Boolean.TRUE).setName(FileUtil.mainName(fileInfo.getFileName()));
    }

    @Override
    public boolean uploadMergeChunks(String identifier, int totalChunks, String fileName) {
        IFileStorageClient client = FileFactory.getClient(StorageType.local);
        List<InputStream> inputStreams = client.getInputStreams(identifier, totalChunks);
        //client.

        return false;
    }

    public static void main(String[] args) {
        //String s = "/develop/code/minimalism/tmp/uploads/local/chunks/c3ca3ed9-ea9b-4cc2-a451-164593e2b2721741455218943/16.part";
        //int i = s.lastIndexOf("/");
        //System.err.println(s.substring(0, i + 1));
        //// 获取文件所在的文件夹路径
        //String parent = FileUtil.getParent(s, 1);
        //System.err.println(parent);
        // 获取操作系统名称
        String osName = System.getProperty("os.name");
        // 获取操作系统架构
        String osArch = System.getProperty("os.arch");
        // 获取操作系统版本
        String osVersion = System.getProperty("os.version");

        System.out.println("操作系统名称: " + osName);
        System.out.println("操作系统架构: " + osArch);
        System.out.println("操作系统版本: " + osVersion);

        String s = "/develop/code/minimalism/tmp/uploads/local/chunks/c3ca3ed9-ea9b-4cc2-a451-164593e2b2721741455218943/16.part";
        System.err.println(FileUtil.mainName(s));
        // 使用字符串操作获取父目录路径
        int i = s.lastIndexOf("/");
        String parentPathByString = s.substring(0, i + 1);
        System.err.println("字符串操作获取的父目录路径: " + parentPathByString);

        // 使用 FileUtil.getParent() 获取父目录路径
        String parentPathByFileUtil = FileUtil.getParent(s, 1);
        System.err.println("FileUtil.getParent() 获取的父目录路径: " + parentPathByFileUtil);

        // 标准化路径，将反斜杠替换为斜杠
        String normalizedPath = parentPathByFileUtil.replace("\\", "/");
        System.err.println("标准化后的父目录路径: " + normalizedPath);
    }

}

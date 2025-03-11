package com.minimalism.file.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Maps;
import com.minimalism.config.OSConfig;
import com.minimalism.constant.Constants;
import com.minimalism.exception.GlobalCustomException;
import com.minimalism.file.config.FileUploadConfig;
import com.minimalism.file.domain.FileInfo;
import com.minimalism.file.domain.FilePart;
import com.minimalism.file.service.FileInfoService;
import com.minimalism.file.service.FilePartService;
import com.minimalism.file.service.FileService;
import com.minimalism.file.storage.FileFactory;
import com.minimalism.file.storage.IFileStorageClient;
import com.minimalism.file.storage.StorageType;
import com.minimalism.result.Result;
import com.minimalism.utils.http.OkHttpUtils;
import com.minimalism.utils.io.IoUtils;
import com.minimalism.utils.jvm.JVMUtils;
import com.minimalism.utils.object.ObjectUtils;
import com.minimalism.utils.oss.LocalOSSUtils;
import com.minimalism.vo.PartVo;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.*;
import java.net.URL;
import java.util.*;
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
    public PartVo partToInputStream(PartVo partVo) {
        InputStream inputStream = null;
        Boolean local = partVo.getLocal();
        String instanceId = null;
        if (ObjectUtils.defaultIfEmpty(local, Boolean.FALSE)) {
            String path = partVo.getLocalResource();
            if (FileUtil.newFile(path).isFile()) {
                String uploadDir = LocalOSSUtils.getUploadDir();
                int index = 0;
                if (path.startsWith(uploadDir)) {
                    index = path.indexOf(uploadDir) + uploadDir.length();
                }
                int endIndex = path.indexOf(FileUtil.getName(path));
                String folder = path.substring(index, endIndex);
                String identifier = partVo.getIdentifier();
                if (folder.contains(identifier)) {
                    folder = folder.replace(identifier, "");
                }
                instanceId = LocalOSSUtils.getRedisInstanceId(folder);
                if (!ObjectUtils.equals(instanceId, FileUploadConfig.getInstanceId())) {
                    String url = FileUploadConfig.getUrlByte(instanceId);

                    Integer chunkNumber = Integer.valueOf(FileUtil.mainName(path));
                    //String identifier,
                    //String folder,
                    //String fileName,
                    //Integer chunkNumber
                    Map<String, Object> params = Maps.newLinkedHashMap();
                    params.put("identifier", identifier);
                    params.put("folder", folder);
                    params.put("chunkNumber", chunkNumber);
                    //params.put("identifier", identifier);
                    String json = OkHttpUtils.get(url, params);
                    Result<List<byte[]>> result = JSONUtil.toBean(json, Result.class);

                    if (!result.validateOk()) {
                        error("获取分片失败,error:{}", result.getMessage());
                        throw new GlobalCustomException(result.getMessage());
                    }
                    byte[] bytes = IoUtils.toByteArray(result.getData().stream().map(ByteArrayInputStream::new).collect(Collectors.toList()));
                    inputStream = new ByteArrayInputStream(bytes);
                } else {
                    inputStream = FileUtil.getInputStream(path);
                }
            }
        } else {
            try {
                inputStream = new URL(partVo.getUrl()).openStream();
            } catch (IOException e) {
                throw new GlobalCustomException(e.getMessage());
            }
        }
        partVo.setInputStream(inputStream).setInstanceId(instanceId);
        return partVo;
    }

    @Override
    public List<InputStream> getPartInputStreamList(String identifier, Long fileId) {
        List<PartVo> partList = getPartList(identifier, fileId).stream().map(o -> o.setIdentifier(identifier)).collect(Collectors.toList());
        List<InputStream> inputStreamList = partList.stream()
                .map(this::partToInputStream).map(PartVo::getInputStream)
                .collect(Collectors.toList());

        return inputStreamList;
    }

    @Override
    public ByteArrayOutputStream mergeOutputStream(String identifier, Long fileId) {

        List<PartVo> partList = getPartList(identifier, fileId).stream().map(o -> o.setIdentifier(identifier)).collect(Collectors.toList());
        List<PartVo> list = partList.stream()
                .map(this::partToInputStream).collect(Collectors.toList());
        ByteArrayOutputStream out = IoUtils.merge(list.stream().map(PartVo::getInputStream).collect(Collectors.toList()));
        //移除其他实例服务器 todo
        // xxx

        String uploadDir = LocalOSSUtils.getUploadDir();


        list.stream().filter(PartVo::getLocal).forEach(part -> {

            String instanceId = part.getInstanceId();
            String localResource = part.getLocalResource();

            int index = 0;
            if (localResource.startsWith(uploadDir)) {
                index = localResource.indexOf(uploadDir) + uploadDir.length();
            }
            int endIndex = localResource.indexOf(FileUtil.getName(localResource));
            String folder = localResource.substring(index, endIndex);

            if (folder.contains(identifier)) {
                folder = folder.replace(identifier, "");
            }
            Integer chunkNumber = null;
            if (FileUtil.newFile(localResource).isFile()) {
                chunkNumber = Integer.valueOf(FileUtil.mainName(localResource));
            }
            String url = FileUploadConfig.getUrlDel(instanceId);
            Map<String, Object> params = Maps.newLinkedHashMap();
            params.put("identifier", identifier);
            params.put("folder", folder);
            params.put("chunkNumber", chunkNumber);
            String json = OkHttpUtils.delete(url, StrUtil.EMPTY_JSON);
            Result result = JSONUtil.toBean(json, Result.class);
            if (!result.validateOk()) {
                error("删除分片失败,error:{}", result.getMessage());
                //使用定时任务兜底
                filePartService.update(Wrappers.lambdaUpdate(FilePart.class)
                        .set(FilePart::getMergeDelete, Boolean.TRUE)
                        .eq(FilePart::getPartCode, identifier));
            }

        });
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
        return FileFactory.getClient(StorageType.local).getMergeFilePath(identifier, fileName + suffix);
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
                long freeMemory = JVMUtils.freeMemory();
                if (freeMemory < fileInfoSize) {
                    //如果内存不够,直接移除文件
                    //方案一
                    fileInfoService.removeById(fileId);
                    List<FilePart> fileParts = filePartService.list(Wrappers.lambdaQuery(FilePart.class).eq(FilePart::getFileId, fileId));
                    fileParts.stream().forEach(part -> {
                        filePartService.removeById(part.getPartId());
                        if (part.getLocal()) {
                            FileUtil.del(part.getLocalResource());
                            String instanceId = FileUploadConfig.getInstanceId();
                        }
                    });

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
            path = getMergePartPath(identifier, "tmp_" + mainName, "." + FileUtil.getSuffix(fileName));
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
                LocalOSSUtils.putRedisFile(fileInfo.getUrl());
            }
            mergeOk(identifier, fileId);
        } finally {
            FileUtil.del(path);
            //FileUtil.del(path.substring(0, path.lastIndexOf(OSType.getSeparator(null)) + 1));
        }
        return true;
    }

    /**
     * 合并分片
     *
     * @param fileId
     * @param identifier
     * @throws IOException
     */
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
            if (CollUtil.isEmpty(parts)) {
                return;
            }
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
            parts.stream().filter(FilePart::getLocal)
                    .map(FilePart::getLocalResource)
                    .forEach(FileUtil::del);

            filePartService.removeByIds(parts.stream().map(FilePart::getPartId).collect(Collectors.toList()));

            FilePart filePart = parts.stream().findFirst().get();
            info("{}" + Constants.PART_SUFFIX + "~{}" + Constants.PART_SUFFIX + ",合并完成",
                    FileUtil.mainName(filePart.getLocal() ? filePart.getLocalResource() : filePart.getUrl()),
                    FileUtil.mainName(parts.get(parts.size() - 1).getLocal() ?
                            parts.get(parts.size() - 1).getLocalResource() : parts.get(parts.size() - 1).getUrl()));

            if (filePart.getLocal()) {
                String partFileName = i + Constants.PART_SUFFIX;
                String localResource = filePart.getLocalResource();
                // 使用字符串操作获取父目录路径

                // 只支持win,mac,linux版本
                int lastIndexOf = localResource.lastIndexOf(OSConfig.getSeparator(filePart.getOsType()));
                String parentPathByString = localResource.substring(0, lastIndexOf + 1);
                String pathAll = parentPathByString + partFileName;
                File file = FileUtil.newFile(pathAll);
                if (!file.exists()) {
                    file.createNewFile();
                }

                IoUtils.copy(new ByteArrayInputStream(outputStream.toByteArray()), FileUtil.getOutputStream(file));
                long size = IoUtils.size(FileUtil.getInputStream(pathAll));

                filePart.setPartSize(size);
                filePartService.save(filePart.setPartId(null).setLocalResource(pathAll));
                excludePartIds.add(filePart.getPartId());
            }
        }

        if (partCount > maxPartSize) {
            //应该递归 todo：
            mergeMore(fileId, identifier);
            //临时抛出异常处理
            //throw new GlobalCustomException("文件过大，暂不支持大文件上传！");
        }
    }

    public FileInfo uploadMergeChunks(InputStream inputStream, String fileMainName, String identifier) {
        IFileStorageClient client = FileFactory.getClient(StorageType.local);
        //FileInfo fileInfo = client.uploadMergeChunks(inputStream, fileMainName, identifier);
        FileInfo fileInfo = client.uploadSharding(fileMainName, inputStream, identifier);
        return fileInfo.setLocal(Boolean.TRUE).setName(FileUtil.mainName(fileInfo.getFileName()));
    }

    @Override
    public boolean uploadMergeChunks(String identifier, int totalChunks, String fileName) {
        IFileStorageClient client = FileFactory.getClient(StorageType.local);
        List<InputStream> inputStreams = client.getInputStreams(identifier, totalChunks);
        //client.

        return false;
    }

    @Override
    public List<byte[]> getByteByLocal(String fileName, String folder, String identifier, Integer chunkNumber) {
        List<byte[]> list = CollUtil.newArrayList();
        String uploadDir = LocalOSSUtils.getUploadDir();

        boolean notBlank = StrUtil.isNotBlank(fileName);

        if (notBlank) {
            if (!fileName.startsWith(uploadDir)) {
                fileName = uploadDir + fileName;
            }
            File file = FileUtil.newFile(fileName);
            if (!file.exists()) {
                throw new GlobalCustomException("文件不存在");
            }
            byte[] bytes = IoUtils.toByteArray(FileUtil.getInputStream(file));
            list.add(bytes);
        }

        boolean isFile = (!notBlank) && StrUtil.isNotBlank(folder) && ObjectUtils.isNotEmpty(chunkNumber);
        if (!folder.startsWith(uploadDir)) {
            folder = uploadDir + folder + OSConfig.separator;
        }
        String separator = OSConfig.separator;
        String two = separator + separator;
        folder = (folder).replace(two, separator).replace(two, separator);
        if (isFile) {
            //文件
            if (!fileName.startsWith(folder)) {
                fileName = folder + fileName;
            }
            File file = FileUtil.newFile(fileName);
            if (!file.exists()) {
                throw new GlobalCustomException("文件不存在");
            }
            byte[] bytes = IoUtils.toByteArray(FileUtil.getInputStream(file));
            list.add(bytes);
        } else if ((!notBlank) && StrUtil.isNotBlank(folder) && ObjectUtils.isEmpty(chunkNumber)) {
            //文件夹
            File file = FileUtil.newFile(folder);
            if (!file.exists()) {
                throw new GlobalCustomException("文件夹不存在");
            }
            Arrays.stream(file.listFiles()).map(FileUtil::getInputStream).map(IoUtils::toByteArray).forEach(list::add);
        } else {
            throw new GlobalCustomException("非法请求");
        }
        return list;
    }

    @Override
    public boolean delByteByLocal(String fileName, String folder, String identifier, Integer chunkNumber) {

        String uploadDir = LocalOSSUtils.getUploadDir();
        boolean notBlank = StrUtil.isNotBlank(fileName);
        if (notBlank) {
            if (!fileName.startsWith(uploadDir)) {
                fileName = uploadDir + fileName;
            }
            File file = FileUtil.newFile(fileName);
            FileUtil.del(file);
        }
        boolean isFile = (!notBlank) && StrUtil.isNotBlank(folder) && ObjectUtils.isNotEmpty(chunkNumber);
        if (!folder.startsWith(uploadDir)) {
            folder = uploadDir + folder;
        }
        String separator = OSConfig.separator;
        String two = separator + separator;
        folder = (folder + separator).replace(two, separator).replace(two, separator);
        if (isFile) {
            //文件
            if (!fileName.startsWith(folder)) {
                fileName = folder + fileName;
            }
            File file = FileUtil.newFile(fileName);
            FileUtil.del(file);
        } else if ((!notBlank) && StrUtil.isNotBlank(folder) && ObjectUtils.isEmpty(chunkNumber)) {
            //文件夹
            File file = FileUtil.newFile(folder);
            FileUtil.del(file);
        }
        return true;
    }

}

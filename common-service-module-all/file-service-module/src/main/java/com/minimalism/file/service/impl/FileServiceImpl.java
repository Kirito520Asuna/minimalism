package com.minimalism.file.service.impl;

import cn.hutool.core.collection.CollUtil;
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
import com.minimalism.utils.file.FileUtils;
import com.minimalism.utils.http.FileHelper;
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
import java.nio.charset.StandardCharsets;
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
            if (FileUtils.isFile(path)) {
                String uploadDir = partVo.getUploadDir();
                int index = 0;
                if (path.startsWith(uploadDir)) {
                    index = path.indexOf(uploadDir) + uploadDir.length();
                }
                int endIndex = path.indexOf(FileUtils.getName(path));
                String folder = path.substring(index, endIndex);
                String identifier = partVo.getIdentifier();
                if (folder.contains(identifier)) {
                    String separator = OSConfig.separator;
                    folder = StrUtil.subBefore(folder, identifier, true);
                }
                instanceId = LocalOSSUtils.getRedisInstanceId(path);
                String cuInstanceId = FileUploadConfig.getInstanceId();
                if (ObjectUtils.equals(instanceId, cuInstanceId)) {
                    inputStream = FileUtils.getInputStream(path);
                } else {
                    String url = FileUploadConfig.getUrlByte(instanceId);

                    Integer chunkNumber = Integer.valueOf(FileUtils.mainName(path));
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
                    Result<List<String>> result = JSONUtil.toBean(json, Result.class);

                    if (!result.validateOk()) {
                        error("获取分片失败,error:{}", result.getMessage());
                        throw new GlobalCustomException(result.getMessage());
                    }
                    List<String> data = result.getData();
                    if (CollUtil.isEmpty(data)) {
                        String err = "文件不存在";
                        error("获取分片失败,error:{}", err);
                        throw new GlobalCustomException(result.getMessage());
                    } else if (data.size() == 1) {
                        // 将 data 数组中的 Base64 编码字符串转换为 byte[]
                        inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data.get(0)));
                    } else {
                        // 将 data 数组中的 Base64 编码字符串转换为 byte[]
                        byte[] bytes = IoUtils.toByteArray(data.stream().map(str -> Base64.getDecoder().decode(str)).map(ByteArrayInputStream::new).collect(Collectors.toList()));
                        inputStream = new ByteArrayInputStream(bytes);
                    }
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

        //String uploadDir = LocalOSSUtils.getUploadDir();
        String cuInstanceId = FileUploadConfig.getInstanceId();

        list.stream().filter(PartVo::getLocal).forEach(part -> {
            String uploadDir = part.getUploadDir();
            String instanceId = part.getInstanceId();
            String localResource = part.getLocalResource();

            if (ObjectUtils.equals(instanceId, cuInstanceId)) {
                FileUtils.del(localResource);
                LocalOSSUtils.delRedisFile(localResource);
                filePartService.remove(Wrappers.lambdaQuery(FilePart.class)
                        .eq(FilePart::getPartCode, identifier)
                        .eq(FilePart::getLocalResource, localResource));
            } else {
                int index = 0;
                if (localResource.startsWith(uploadDir)) {
                    index = localResource.indexOf(uploadDir) + uploadDir.length();
                }
                int endIndex = localResource.indexOf(FileUtils.getName(localResource));
                String folder = localResource.substring(index, endIndex);

                if (folder.contains(identifier)) {
                    folder = StrUtil.subBefore(folder, identifier, true);
                }
                Integer chunkNumber = null;
                if (FileUtils.isFile(localResource)) {
                    chunkNumber = Integer.valueOf(FileUtils.mainName(localResource));
                }
                String url = FileUploadConfig.getUrlDel(instanceId);
                Map<String, Object> params = Maps.newLinkedHashMap();
                params.put("identifier", identifier);
                params.put("folder", folder);
                params.put("chunkNumber", chunkNumber);
                String json = OkHttpUtils.delete(url, params);
                Result result = JSONUtil.toBean(json, Result.class);
                if (!result.validateOk()) {
                    error("删除分片失败,error:{}", result.getMessage());
                    //使用定时任务兜底
                    filePartService.update(Wrappers.lambdaUpdate(FilePart.class)
                            .set(FilePart::getMergeDelete, Boolean.TRUE)
                            .eq(FilePart::getPartCode, identifier));
                }
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
            mainName = FileUtils.mainName(fileInfo.getFileName());
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
                            FileUtils.del(part.getLocalResource());
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
            mainName = FileUtils.mainName(fileName);
            suffix = FileUtils.getSuffix(fileName);
            path = getMergePartPath(identifier, "tmp_" + mainName, "." + FileUtils.getSuffix(fileName));
        }

        if (!suffix.startsWith(".")) {
            suffix = "." + suffix;
        }

        fileMainName = mainName + suffix;
        //生成临时文件
        File tmpFile = FileUtils.newFile(path);
        if (!tmpFile.exists()) {
            tmpFile.createNewFile();
        }

        try {
            ByteArrayOutputStream outputStream = mergeOutputStream(identifier, fileId);
            InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            OutputStream fileOutputStream = FileUtils.getOutputStream(tmpFile);
            IoUtils.copy(inputStream, fileOutputStream);
            inputStream = FileUtils.getInputStream(path);
            FileInfo fileInfo = uploadMergeChunks(inputStream, fileMainName, identifier);
            if (fileId != null) {
                //FileInfo fileInfoById = fileInfoService.getById(fileId);
                //fileInfoById.setUrl(fileInfo.getUrl())
                //        .setLocal(fileInfo.getLocal());
                fileInfoService.update(Wrappers.lambdaUpdate(FileInfo.class)
                        .set(FileInfo::getUrl, fileInfo.getUrl())
                        .set(FileInfo::getLocal, fileInfo.getLocal())
                        .eq(FileInfo::getFileId, fileId));
                LocalOSSUtils.putRedisFile(fileInfo.getUrl());
            }
            mergeOk(identifier, fileId);
        } finally {
            FileUtils.del(path);
            //FileUtils.del(path.substring(0, path.lastIndexOf(OSType.getSeparator(null)) + 1));
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
                    inputStream = FileUtils.getInputStream(part.getLocalResource());
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
                    .forEach(FileUtils::del);

            filePartService.removeByIds(parts.stream().map(FilePart::getPartId).collect(Collectors.toList()));

            FilePart filePart = parts.stream().findFirst().get();
            info("{}" + Constants.PART_SUFFIX + "~{}" + Constants.PART_SUFFIX + ",合并完成",
                    FileUtils.mainName(filePart.getLocal() ? filePart.getLocalResource() : filePart.getUrl()),
                    FileUtils.mainName(parts.get(parts.size() - 1).getLocal() ?
                            parts.get(parts.size() - 1).getLocalResource() : parts.get(parts.size() - 1).getUrl()));

            if (filePart.getLocal()) {
                String partFileName = i + Constants.PART_SUFFIX;
                String localResource = filePart.getLocalResource();
                // 使用字符串操作获取父目录路径

                // 只支持win,mac,linux版本
                int lastIndexOf = localResource.lastIndexOf(OSConfig.getSeparator(filePart.getOsType()));
                String parentPathByString = localResource.substring(0, lastIndexOf + 1);
                String pathAll = parentPathByString + partFileName;
                File file = FileUtils.newFile(pathAll);
                if (!file.exists()) {
                    file.createNewFile();
                }

                IoUtils.copy(new ByteArrayInputStream(outputStream.toByteArray()), FileUtils.getOutputStream(file));
                long size = IoUtils.size(FileUtils.getInputStream(pathAll));

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
        return fileInfo.setLocal(Boolean.TRUE).setName(FileUtils.mainName(fileInfo.getFileName()));
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
        String filePath = fileName;
        String fileDir = folder;

        boolean notBlank = StrUtil.isNotBlank(fileName);

        if (notBlank) {
            if (!fileName.startsWith(uploadDir)) {
                filePath = uploadDir + fileName;
            }
            String instanceId = LocalOSSUtils.getRedisInstanceId(filePath);
            if (FileUploadConfig.isCurrentInstance(instanceId)) {
                File file = FileUtils.newFile(filePath);
                if (!file.exists()) {
                    throw new GlobalCustomException("文件不存在");
                }
                byte[] bytes = IoUtils.toByteArray(FileUtils.getInputStream(file));
                list.add(bytes);
            } else {
                info("文件不在本机，正在获取...");
                list.addAll(FileHelper.getBytesByRemote(instanceId, fileName, folder, identifier, chunkNumber));
            }
            return list;
        }
        boolean isFile = (!notBlank) && StrUtil.isNotBlank(folder) && ObjectUtils.isNotEmpty(chunkNumber);
        String separator = OSConfig.separator;
        if (!fileDir.startsWith(uploadDir)) {
            fileDir = uploadDir + fileDir + separator;
        }
        String two = separator + separator;
        fileDir = (fileDir).replace(two, separator).replace(two, separator);
        if (isFile) {
            String instanceId = LocalOSSUtils.getRedisInstanceId(filePath);
            if (FileUploadConfig.isCurrentInstance(instanceId)) {
                //文件
                filePath = fileDir;
                if (!filePath.endsWith(separator)) {
                    filePath = filePath + separator;
                }
                if (StrUtil.isNotBlank(identifier)) {
                    filePath = filePath + identifier + separator + chunkNumber + Constants.PART_SUFFIX;
                }
                filePath = filePath.replace(two, separator).replace(two, separator);
                File file = FileUtils.newFile(filePath);
                if (!file.exists()) {
                    throw new GlobalCustomException("文件不存在");
                }
                byte[] bytes = IoUtils.toByteArray(FileUtils.getInputStream(file));
                list.add(bytes);
            } else {
                info("文件不在本机，正在获取...");
                list.addAll(FileHelper.getBytesByRemote(instanceId, fileName, folder, identifier, chunkNumber));
            }
        } else if ((!notBlank) && StrUtil.isNotBlank(folder) && ObjectUtils.isEmpty(chunkNumber)) {
            String instanceId = LocalOSSUtils.getRedisInstanceId(fileDir);
            if (FileUploadConfig.isCurrentInstance(instanceId)) {
                //文件夹
                File file = FileUtils.newFile(fileDir);
                if (!file.exists()) {
                    throw new GlobalCustomException("文件夹不存在");
                }
                Arrays.stream(file.listFiles()).map(FileUtils::getInputStream).map(IoUtils::toByteArray).forEach(list::add);
            } else {
                info("文件不在本机，正在获取...");
                list.addAll(FileHelper.getBytesByRemote(instanceId, fileName, folder, identifier, chunkNumber));
            }
        } else {
            throw new GlobalCustomException("非法请求");
        }
        return list;
    }

    @Override
    public boolean delByteByLocal(String fileName, String folder, String identifier, Integer chunkNumber) {
        String uploadDir = LocalOSSUtils.getUploadDir();
        boolean notBlank = StrUtil.isNotBlank(fileName);
        String filePath = fileName;
        String fileDir = folder;
        if (notBlank) {
            if (!fileName.startsWith(uploadDir)) {
                filePath = uploadDir + fileName;
            }
            String instanceId = LocalOSSUtils.getRedisInstanceId(filePath);
            if (FileUploadConfig.isCurrentInstance(instanceId)) {
                FileUtils.del(filePath);
                LocalOSSUtils.delRedisFile(filePath);
            } else {
                info("文件不在本机，正在删除...");
                FileHelper.delBytesByRemote(instanceId, fileName, folder, identifier, chunkNumber);
            }
        }
        boolean isFile = (!notBlank) && StrUtil.isNotBlank(folder) && ObjectUtils.isNotEmpty(chunkNumber);
        String separator = OSConfig.separator;
        if (!fileDir.startsWith(uploadDir)) {
            fileDir = uploadDir + fileDir + separator;
        }
        String two = separator + separator;
        fileDir = (fileDir + separator).replace(two, separator).replace(two, separator);
        if (isFile) {
            //文件
            filePath = fileDir;
            if (!filePath.endsWith(separator)) {
                filePath = filePath + separator;
            }
            if (StrUtil.isNotBlank(identifier)) {
                filePath = filePath + identifier + separator + chunkNumber + Constants.PART_SUFFIX;
            }
            filePath = filePath.replace(two, separator).replace(two, separator);
            String instanceId = LocalOSSUtils.getRedisInstanceId(filePath);
            if (FileUploadConfig.isCurrentInstance(instanceId)) {
                File file = FileUtils.newFile(filePath);
                FileUtils.del(file);
                LocalOSSUtils.delRedisFile(filePath);

                String dir = FileUtils.getName(filePath);
                dir = StrUtil.subBefore(filePath, dir, true);
                List<File> files = FileUtils.loopFiles(dir);
                if (files.size() == 0) {
                    FileUtils.del(dir);
                    LocalOSSUtils.delRedisFile(dir);
                }
            } else {
                info("文件不在本机，正在删除...");
                FileHelper.delBytesByRemote(instanceId, fileName, folder, identifier, chunkNumber);
            }
        } else if ((!notBlank) && StrUtil.isNotBlank(folder) && ObjectUtils.isEmpty(chunkNumber)) {
            String instanceId = LocalOSSUtils.getRedisInstanceId(fileDir);
            if (FileUploadConfig.isCurrentInstance(instanceId)) {
                //文件夹
                FileUtils.del(fileDir);
                LocalOSSUtils.delRedisFile(fileDir);
            } else {
                info("文件不在本机，正在删除...");
                FileHelper.delBytesByRemote(instanceId, fileName, folder, identifier, chunkNumber);
            }
        } else {
            throw new GlobalCustomException("非法操作");
        }
        return true;
    }

}

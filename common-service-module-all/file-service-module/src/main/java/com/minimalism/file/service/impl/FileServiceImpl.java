package com.minimalism.file.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.minimalism.config.OSConfig;
import com.minimalism.constant.Constants;
import com.minimalism.exception.BusinessException;
import com.minimalism.exception.GlobalCustomException;
import com.minimalism.file.config.FileUploadConfig;
import com.minimalism.file.domain.FileInfo;
import com.minimalism.file.domain.FilePart;
import com.minimalism.file.properties.FileProperties;
import com.minimalism.file.service.FileInfoService;
import com.minimalism.file.service.FilePartService;
import com.minimalism.file.service.FileService;
import com.minimalism.file.storage.FileFactory;
import com.minimalism.file.storage.IFileStorageClient;
import com.minimalism.file.storage.StorageType;
import com.minimalism.utils.file.FileUtils;
import com.minimalism.utils.http.FileHelper;
import com.minimalism.utils.io.IoUtils;
import com.minimalism.utils.jvm.JVMUtils;
import com.minimalism.utils.object.ObjectUtils;
import com.minimalism.utils.oss.LocalOSSUtils;
import com.minimalism.vo.PartVo;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
                    //String separator = OSConfig.separator;
                    folder = StrUtil.subBefore(folder, identifier, true);
                }
                instanceId = LocalOSSUtils.getRedisInstanceId(path);
                if (FileUploadConfig.isCurrentInstance(instanceId)) {
                    inputStream = FileUtils.getInputStream(path);
                } else {
                    Integer chunkNumber = Integer.valueOf(FileUtils.mainName(path));
                    try {
                        List<byte[]> bytesByRemote = FileHelper.getBytesByRemote(instanceId, null, folder, identifier, chunkNumber);
                        byte[] bytes = IoUtils.toByteArray(bytesByRemote.stream().map(ByteArrayInputStream::new).collect(Collectors.toList()));
                        inputStream = new ByteArrayInputStream(bytes);
                    } catch (BusinessException e) {
                        error("获取分片失败,文件不存在,error:{}", e.getMessage());
                        throw new GlobalCustomException(e.getMessage());
                    } catch (GlobalCustomException e) {
                        error("获取分片失败,error:{}", e.getMessage());
                        throw e;
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
    public List<PartVo> getPartList(String identifier, Long fileId) {
        return filePartService.getPartList(identifier, fileId);
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
        //上传到云端
        StorageType type = SpringUtil.getBean(FileProperties.class).getType();
        IFileStorageClient client = FileFactory.getClient(type);
        FilePart filePart = client.uploadShardingChunkNumber(chunkNumber, identifier, inputStream).setFileId(fileId);
        filePartService.save(filePart);
        return true;
    }


    public FileInfo uploadMergeChunks(InputStream inputStream, String fileMainName, String identifier) {
        //上传到云端
        StorageType storageType = SpringUtil.getBean(FileProperties.class).getType();
        IFileStorageClient client = FileFactory.getClient(storageType);
        //FileInfo fileInfo = client.uploadMergeChunks(inputStream, fileMainName, identifier);
        FileInfo fileInfo = client.uploadSharding(fileMainName, inputStream, identifier);
        return fileInfo.setLocal(Boolean.TRUE).setName(FileUtils.mainName(fileInfo.getFileName()));
    }

    @Override
    public boolean uploadMergeChunks(String identifier, int totalChunks, String fileName) {
        //上传到云端
        StorageType storageType = SpringUtil.getBean(FileProperties.class).getType();
        IFileStorageClient client = FileFactory.getClient(storageType);
        List<InputStream> inputStreams = client.getInputStreams(identifier, totalChunks);
        //client.

        return false;
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

    @Override
    public ByteArrayOutputStream mergeOutputStream(String identifier, Long fileId) {

        List<PartVo> partList = getPartList(identifier, fileId).stream().map(o -> o.setIdentifier(identifier)).collect(Collectors.toList());
        List<PartVo> list = partList.stream()
                .map(this::partToInputStream).collect(Collectors.toList());
        ByteArrayOutputStream out = IoUtils.merge(list.stream().map(PartVo::getInputStream).collect(Collectors.toList()));
        //移除其他实例服务器
        list.stream().filter(PartVo::getLocal).forEach(part -> {
            String uploadDir = part.getUploadDir();
            String instanceId = part.getInstanceId();
            String localResource = part.getLocalResource();

            if (FileUploadConfig.isCurrentInstance(instanceId)) {
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
                try {
                    FileHelper.delBytesByRemote(instanceId, identifier, folder, identifier, chunkNumber);
                } catch (GlobalCustomException e) {
                    error("删除分片失败,error:{}", e.getMessage());
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

    @Data
    @SuperBuilder
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    static class FileByte {
        private String fileName;
        private byte[] bytes;
    }

    @SneakyThrows
    FileByte getBytes(String identifier, boolean isPart, Integer partSort) {
        byte[] bytes = null;
        String fileName = null;
        if (isPart) {
            fileName = partSort + Constants.PART_SUFFIX;
            FilePart filePart = filePartService.getByCode(identifier, partSort);
            if (filePart.getLocal()) {
                String localResource = filePart.getLocalResource();
                if (FileUtils.isFile(localResource)) {
                    fileName = FileUtils.getName(localResource);
                }
                String redisInstanceId = LocalOSSUtils.getRedisInstanceId(localResource);

                if (FileUploadConfig.isCurrentInstance(redisInstanceId)) {
                    bytes = IoUtils.toByteArray(FileUtils.getInputStream(localResource));
                } else {
                    String uploadDir = filePart.getUploadDir();
                    String subBefore = StrUtil.subBefore(localResource, identifier, true);
                    String folder = subBefore.substring(uploadDir.length(), subBefore.length());

                    List<byte[]> bytesByRemote = FileHelper.getBytesByRemote(redisInstanceId, fileName, folder, identifier, partSort);
                    bytes = bytesByRemote.get(0);
                }

            } else {
                InputStream inputStream = new URL(filePart.getUrl()).openStream();
                bytes = IoUtils.toByteArray(inputStream);
            }
        } else {
            FileInfo fileInfo = fileInfoService.getByPartCode(identifier);
            String url = fileInfo.getUrl();
            fileName = fileInfo.getFileName();
            if (fileInfo.getLocal()) {
                String uploadDir = fileInfo.getUploadDir();
                String redisInstanceId = LocalOSSUtils.getRedisInstanceId(url);

                if (FileUploadConfig.isCurrentInstance(redisInstanceId)) {
                    bytes = IoUtils.toByteArray(FileUtils.getInputStream(url));
                } else {
                    String subBefore = StrUtil.subBefore(url, identifier, true);
                    String folder = subBefore.substring(uploadDir.length(), subBefore.length());

                    List<byte[]> bytesByRemote = FileHelper.getBytesByRemote(redisInstanceId, fileName, folder, identifier, null);
                    bytes = bytesByRemote.get(0);
                }
            } else {
                InputStream inputStream = new URL(url).openStream();
                bytes = IoUtils.toByteArray(inputStream);
            }
        }
        FileByte fileByte = FileByte.builder().fileName(fileName).bytes(bytes).build();
        return fileByte;
    }

    @SneakyThrows
    @Override
    public void download(String identifier, boolean isPart, Integer partSort, HttpServletResponse response) {
        String fileName = StrUtil.EMPTY;

        if (isPart) {
            fileName = partSort + Constants.PART_SUFFIX;
        }

        fileName = FileUtils.getName(fileName);
        FileByte fileByte = getBytes(identifier, isPart, partSort);
        fileName = fileByte.getFileName();
        byte[] bytes = fileByte.getBytes();
        int fileSize = bytes.length;

        InputStream inputStream = new ByteArrayInputStream(bytes);
        //InputStream inputStream = MinioOSSUtils.downloadMinioInput(minioClient, bucketName, objectName);
        OutputStream responseOutputStream = response.getOutputStream();
        // 将 OSS 输出流的内容复制到 HTTP 响应输出流中
        IoUtils.copy(inputStream, responseOutputStream);
        // 设置文件ContentType类型，这样设置，会自动判断下载文件类型
        response.setHeader("Content-Length", String.valueOf(fileSize));
        response.setContentType("application/x-msdownload");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(fileName, String.valueOf(StandardCharsets.UTF_8)));
        response.flushBuffer();
        IoUtils.close(responseOutputStream);
    }

    @SneakyThrows
    @Override
    public void downloadExecutor(String identifier, boolean isPart, Integer partSort, HttpServletResponse response) {
        FileByte fileByte = getBytes(identifier, isPart, partSort);
        String fileName = fileByte.getFileName();
        byte[] bytes = fileByte.getBytes();
        int fileSize = bytes.length;

        // 计算分块大小，比如每块 1MB
        int chunkSize = 10 * 1024 * 1024;
        int numChunks = (fileSize + chunkSize - 1) / chunkSize;

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, String.valueOf(StandardCharsets.UTF_8)));
        response.setHeader("Content-Length", String.valueOf(fileSize));

        response.setContentType("application/octet-stream");

        OutputStream outputStream = response.getOutputStream();
        ExecutorService executor = Executors.newFixedThreadPool(4); // 4 个线程

        List<Future<byte[]>> futures = new ArrayList<>();

        for (int i = 0; i < numChunks; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, fileSize);

            // 异步任务读取文件片段
            Future<byte[]> future = executor.submit(() -> Arrays.copyOfRange(bytes, start, end));
            futures.add(future);
        }

        for (Future<byte[]> future : futures) {
            outputStream.write(future.get()); // 等待数据读取完毕
        }

        outputStream.flush();
        executor.shutdown();
        IoUtils.close(outputStream);
    }

    @SneakyThrows
    @Override
    public void downloadMon(String identifier, boolean isPart, Integer partSort,
                            HttpServletRequest request, HttpServletResponse response) {
        // 获取文件名和文件内容
        String fileName = StrUtil.EMPTY;
        if (isPart) {
            fileName = partSort + Constants.PART_SUFFIX;
        }
        fileName = FileUtils.getName(fileName);
        FileByte fileByte = getBytes(identifier, isPart, partSort);
        fileName = fileByte.getFileName();
        byte[] bytes = fileByte.getBytes();
        long fileSize = bytes.length;

        // 设置基础响应头
        response.setContentType("application/x-msdownload");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode(fileName, String.valueOf(StandardCharsets.UTF_8)));
        response.setHeader("Accept-Ranges", "bytes"); // 声明支持断点续传

        // 解析Range请求头
        String rangeHeader = request.getHeader("Range");
        long start = 0;
        long end = fileSize - 1;
        boolean hasRange = false;

        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            try {
                // 解析范围值
                String range = rangeHeader.substring("bytes=".length());
                String[] ranges = range.split("-");
                start = Long.parseLong(ranges[0]);
                if (ranges.length > 1 && !ranges[1].isEmpty()) {
                    end = Long.parseLong(ranges[1]);
                } else {
                    end = fileSize - 1; // 处理类似 bytes=1000- 的情况
                }

                // 验证范围有效性
                if (start < 0 || end >= fileSize || start > end) {
                    response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                    response.setHeader("Content-Range", "bytes */" + fileSize);
                    return;
                }

                hasRange = true;
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                response.setHeader("Content-Range",
                        "bytes " + start + "-" + end + "/" + fileSize);
                response.setHeader("Content-Length",
                        String.valueOf(end - start + 1));
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Range Header");
                return;
            }
        } else {
            // 无Range头时返回完整文件
            response.setHeader("Content-Length", String.valueOf(fileSize));
        }

        // 使用try-with-resources自动关闭流
        try (InputStream inputStream = new ByteArrayInputStream(bytes);
             OutputStream outputStream = response.getOutputStream()) {

            // 跳过起始字节
            long bytesToSkip = start;
            while (bytesToSkip > 0) {
                long skipped = inputStream.skip(bytesToSkip);
                if (skipped == 0) break;
                bytesToSkip -= skipped;
            }

            // 分段写入响应流
            byte[] buffer = new byte[4096];
            long remaining = end - start + 1;
            int readLength;

            while (remaining > 0 &&
                    (readLength = inputStream.read(buffer, 0,
                            (int) Math.min(buffer.length, remaining))) != -1) {
                outputStream.write(buffer, 0, readLength);
                remaining -= readLength;
            }
        } catch (IOException e) {
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "File streaming error");
            }
            throw e;
        }
    }


    private static final int BUFFER_SIZE = 4096; // 4KB缓冲区
    private static final String CONTENT_DISPOSITION_FORMAT = "attachment;filename=\"%s\";filename*=UTF-8''%s";

    public void download(String identifier, boolean isPart, Integer partSort,
                         HttpServletRequest request, HttpServletResponse response) {
        // 参数校验
        if (identifier == null || identifier.isEmpty()) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid identifier");
            return;
        }

        try {
            // 获取文件元数据和内容
            FileMeta fileMeta = resolveFileMeta(identifier, isPart, partSort);
            byte[] fileContent = getFileContent(fileMeta);
            long fileSize = fileContent.length;

            // 设置通用响应头
            setCommonHeaders(response, fileMeta.getFileName(), fileSize);

            // 处理Range请求
            Range range = parseRangeHeader(response, request, fileSize);
            if (!range.isValid()) {
                sendError(response, HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE,
                        "Invalid range: " + range);
                return;
            }

            // 流式传输文件内容
            try (InputStream inputStream = new ByteArrayInputStream(fileContent);
                 OutputStream outputStream = response.getOutputStream()) {

                streamFileContent(inputStream, outputStream, range);
            }
        } catch (ClientAbortException e) {
            handleClientAbort(e); // 客户端主动中断的特殊处理
        } catch (IOException e) {
            handleIOException(e, response);
        } catch (Exception e) {
            handleGenericException(e, response);
        }
    }

    // region 核心逻辑方法
    private void setCommonHeaders(HttpServletResponse response, String fileName, long fileSize)
            throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/octet-stream");
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Length", String.valueOf(fileSize));

        // RFC 5987编码规范处理文件名
        String encodedFileName = URLEncoder.encode(fileName, String.valueOf(StandardCharsets.UTF_8))
                .replace("+", "%20");
        response.setHeader("Content-Disposition",
                String.format(CONTENT_DISPOSITION_FORMAT, fileName, encodedFileName));
    }

    private Range parseRangeHeader(HttpServletResponse response, HttpServletRequest request, long fileSize) {
        String rangeHeader = request.getHeader("Range");
        if (rangeHeader == null || !rangeHeader.startsWith("bytes=")) {
            return new Range(0, fileSize - 1, fileSize); // 完整文件
        }

        try {
            String rangeValue = rangeHeader.substring(6);
            String[] parts = rangeValue.split("-");
            long start = Long.parseLong(parts[0]);
            long end = parts.length > 1 && !parts[1].isEmpty() ?
                    Long.parseLong(parts[1]) : fileSize - 1;

            // 范围校验
            end = Math.min(end, fileSize - 1);
            start = Math.max(start, 0);

            if (start > end) {
                return Range.INVALID;
            }

            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            response.setHeader("Content-Range",
                    String.format("bytes %d-%d/%d", start, end, fileSize));
            response.setHeader("Content-Length",
                    String.valueOf(end - start + 1));

            return new Range(start, end, fileSize);
        } catch (NumberFormatException e) {
            warn("Invalid Range header: {}", rangeHeader);
            return Range.INVALID;
        }
    }

    private void streamFileContent(InputStream inputStream, OutputStream outputStream, Range range)
            throws IOException {
        long bytesToSkip = range.start;
        long remaining = range.end - range.start + 1;

        // 跳过已下载部分
        while (bytesToSkip > 0) {
            long skipped = inputStream.skip(bytesToSkip);
            if (skipped <= 0) break;
            bytesToSkip -= skipped;
        }

        // 分块传输
        byte[] buffer = new byte[BUFFER_SIZE];
        while (remaining > 0) {
            int readSize = (int) Math.min(buffer.length, remaining);
            int bytesRead = inputStream.read(buffer, 0, readSize);
            if (bytesRead == -1) break;

            try {
                outputStream.write(buffer, 0, bytesRead);
                outputStream.flush();
                remaining -= bytesRead;
            } catch (IOException e) {
                if (isClientAbort(e)) {
                    throw new ClientAbortException("Client aborted download", e);
                }
                throw e;
            }
        }
    }
    // endregion

    // region 异常处理
    private boolean isClientAbort(Throwable e) {
        return e instanceof ClientAbortException ||
                (e.getCause() != null && isClientAbort(e.getCause()));
    }

    private void handleClientAbort(ClientAbortException e) {
        debug("客户端中止下载: {}", e.getMessage()); // DEBUG级别日志
    }

    private void handleIOException(IOException e, HttpServletResponse response) {
        if (isClientAbort(e)) {
            handleClientAbort((ClientAbortException) e);
        } else {
            error("文件传输IO异常", e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "文件传输失败");
        }
    }

    private void handleGenericException(Exception e, HttpServletResponse response) {
        error("文件下载系统异常", e);
        sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "系统错误");
    }

    private void sendError(HttpServletResponse response, int sc, String msg) {
        try {
            if (!response.isCommitted()) {
                response.sendError(sc, msg);
            }
        } catch (IOException ex) {
            warn("发送错误响应失败", ex);
        }
    }
    // endregion

    // region 辅助类
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Range {
        static final Range INVALID = new Range(-1, -1, -1);
        private long start;
        private long end;
        private long total;

        boolean isValid() {
            return start >= 0 && end >= start && total > 0;
        }
    }

    // 模拟文件获取逻辑
    private FileMeta resolveFileMeta(String identifier, boolean isPart, Integer partSort) {
        // 实际项目应替换为真实文件元数据获取逻辑
        return new FileMeta("example.txt", new byte[1024]);
    }

    private byte[] getFileContent(FileMeta meta) {
        return meta.content;
    }

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    private static class FileMeta {
        private String fileName;
        private byte[] content;

    }


}

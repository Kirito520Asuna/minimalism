package com.minimalism.utils.oss;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.exception.GlobalCustomException;
import com.minimalism.file.domain.FilePart;
import com.minimalism.file.properties.FileProperties;
import com.minimalism.utils.io.IoUtils;
import com.minimalism.utils.object.ObjectUtils;
import lombok.SneakyThrows;
import org.springframework.core.env.Environment;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2025/3/7 23:35:41
 * @Description
 */
public class LocalOSSUtils {
    public static String getUploadDir() {
        String uploadDir = "/";
        try {
            FileProperties fileProperties = SpringUtil.getBean(FileProperties.class);
            FileProperties.LocalProperties local = fileProperties.getLocal();
            uploadDir = local.getUploadDir();
        } catch (Exception e) {
            uploadDir = SpringUtil.getBean(Environment.class).getProperty("file.properties.local.uploadDir", String.class, "tmp/uploads");
        }
        //uploadDir = "tmp/uploads";

        if (!uploadDir.endsWith("/")) {
            uploadDir = uploadDir + "/";
        }
        uploadDir = uploadDir + getBucket() + "/";
        File file = FileUtil.newFile(uploadDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return uploadDir;
    }

    public static String getBucket() {
        String directory;
        try {
            FileProperties fileProperties = SpringUtil.getBean(FileProperties.class);
            FileProperties.LocalProperties local = fileProperties.getLocal();
            directory = local.getDirectory();
        } catch (Exception e) {
            directory = SpringUtil.getBean(Environment.class).getProperty("file.properties.local.directory");
        }
        return ObjectUtils.defaultIfBlank(directory, "local");
    }

    public static String getChunkDir() {
        String chunk = getUploadDir() + "chunks/";
        File file = FileUtil.newFile(chunk);
        if (!file.exists()) {
            file.mkdirs();
        }
        return chunk;
    }

    public static String getMergeDir() {
        String merged = getUploadDir() + "merged/";
        File file = FileUtil.newFile(merged);
        if (!file.exists()) {
            file.mkdirs();
        }
        return merged;
    }

    public static int getChunkSize() {
        // 每个分片大小为1MB
        return 1024 * 1024;
    }

    public static String getPartSuffix() {
        return ".part";
    }

    public static String getMergeFilePath(String fileMainName) {
        return getMergeDir() + fileMainName;
    }

    public static String getChunkDirPath(String identifier) {
        return getChunkDir() + identifier + "/";
    }

    /*===========================================================================================================================================================================================================================================*/

    /**
     * 上传文件
     *
     * @param flieName
     * @param inputStream
     * @return
     */
    public static String upload(String flieName, InputStream inputStream) {
        return upload(null, flieName, inputStream);
    }

    /**
     * 上传文件
     *
     * @param bucketName
     * @param flieName
     * @param inputStream
     */
    @SneakyThrows
    public static String upload(String bucketName, String flieName, InputStream inputStream) {
        if (StrUtil.isBlank(bucketName)) {
            bucketName = "";
        }
        String bucketPath = getUploadDir() + "/" + bucketName + "/";
        bucketPath = bucketPath.replace("//", "/");

        File bucketFile = FileUtil.newFile(bucketPath);
        if (!bucketFile.exists()) {
            bucketFile.mkdirs();
        }
        File file = FileUtil.newFile(bucketPath + flieName);
        if (!file.exists()) {
            file.createNewFile();
        }
        IoUtils.copy(inputStream, FileUtil.getOutputStream(file));
        return FileUtil.getAbsolutePath(file);
    }

    /**
     * 分片上传
     *
     * @return
     */
    @SneakyThrows
    public static String uploadSharding(String fileName, String identifier, InputStream input) {
        splitFileLocal(identifier, input);
        String fileLocal = mergeFileLocal(fileName, identifier);
        return FileUtil.getAbsolutePath(fileLocal);
    }

    /**
     * 将指定文件拆分成若干个分块文件
     *
     * @param identifier
     * @param input
     */
    public static void splitFileLocal(String identifier, InputStream input) {
        splitFileLocal(getChunkDir(), identifier, input);
    }

    /**
     * 将指定文件拆分成若干个分块文件
     *
     * @param chunkDir   分块文件存放目录
     * @param identifier 唯一值
     * @param input      原始文件输入流
     */
    @SneakyThrows
    public static void splitFileLocal(String chunkDir, String identifier, InputStream input) {
        if (StrUtil.isBlank(chunkDir)) {
            chunkDir = getChunkDir();
        }
        if (!chunkDir.endsWith("/")) {
            chunkDir = chunkDir + "/";
        }
        int chunkNumber = 1;
        // 创建分块存放目录：CHUNK_DIR + identifier
        String chunkDirPath = chunkDir + identifier;
        File chunkDirFile = FileUtil.newFile(chunkDirPath);
        if (!chunkDirFile.exists()) {
            chunkDirFile.mkdirs();
        }

        try (InputStream fis = input) {
            byte[] buffer = new byte[getChunkSize()];
            int bytesRead;
            // 循环读取，直到返回 -1 表示读完
            while ((bytesRead = fis.read(buffer)) != -1) {
                // 每个分块文件命名为 “chunkNumber.part”
                File tempFile = FileUtil.newFile(chunkDirPath + "/" + chunkNumber + getPartSuffix());
                if (!tempFile.exists()) {
                    tempFile.createNewFile();
                }
                // 写入读取的字节到分块文件中
                try (OutputStream os = new FileOutputStream(tempFile)) {
                    os.write(buffer, 0, bytesRead);
                }
                chunkNumber++;
            }
        }
    }

    /**
     * 上传单个分片
     *
     * @param chunkNumber
     * @param identifier
     * @param input
     */
    public static String splitChunkNumberFileLocal(int chunkNumber, String identifier, InputStream input) {
        return splitChunkNumberFileLocal(getChunkDir(), chunkNumber, identifier, input);
    }

    /**
     * 上传单个分片
     *
     * @param chunkDir
     * @param chunkNumber
     * @param identifier
     * @param input
     */
    @SneakyThrows
    public static String splitChunkNumberFileLocal(String chunkDir, int chunkNumber, String identifier, InputStream input) {
        // 创建分块存放目录：CHUNK_DIR + identifier
        String chunkDirPath = chunkDir + identifier;
        File chunkDirFile = FileUtil.newFile(chunkDirPath);
        if (!chunkDirFile.exists()) {
            chunkDirFile.mkdirs();
        }
        File tempFile = FileUtil.newFile(chunkDirPath + "/" + chunkNumber + getPartSuffix());
        if (!tempFile.exists()) {
            tempFile.createNewFile();
        }
        BufferedOutputStream outputStream = FileUtil.getOutputStream(tempFile);
        IoUtils.copy(input, outputStream);
        return FileUtil.getAbsolutePath(tempFile);
    }

    /**
     * 将分块文件合并为一个完整文件，并清理临时分块文件
     *
     * @param fileName
     * @param identifier
     * @return url
     */
    public static String mergeFileLocal(String fileName, String identifier) {
        return mergeFileLocal(getChunkDir(), getMergeDir(), fileName, identifier);
    }

    /**
     * 将分块文件合并为一个完整文件，并清理临时分块文件
     *
     * @param chunkDir   分片文件夹(不包含唯一值)
     * @param mergeDir   合并文件夹
     * @param fileName   原始文件名
     * @param identifier 唯一值
     * @return url
     */
    @SneakyThrows
    public static String mergeFileLocal(String chunkDir, String mergeDir, String fileName, String identifier) {
        if (!chunkDir.endsWith("/")) {
            chunkDir = chunkDir + "/";
        }
        if (!mergeDir.endsWith("/")) {
            mergeDir = mergeDir + "/";
        }
        // 分块文件所在目录
        File chunkDirFile = FileUtil.newFile(chunkDir + identifier);
        File[] chunkFiles = chunkDirFile.listFiles();
        if (chunkFiles == null || chunkFiles.length == 0) {
            throw new RuntimeException("No chunk files found for merging");
        }

        // 按分块编号排序（假设分块文件名为 "1.part", "2.part", …）
        List<File> sortedChunks = Arrays.stream(chunkFiles)
                .sorted(Comparator.comparingInt(f -> Integer.parseInt(FileUtil.mainName(f))))
                .collect(Collectors.toList());

        // 创建合并后文件存放目录
        File mergeDirFile = FileUtil.newFile(mergeDir);
        if (!mergeDirFile.exists()) {
            mergeDirFile.mkdirs();
        }
        // 合并后完整文件路径：MERGE_DIR + 原始文件名
        File mergedFile = FileUtil.newFile(mergeDir + fileName);
        // 如果目标文件已存在，则先删除
        if (mergedFile.exists()) {
            mergedFile.delete();
        }
        mergedFile.createNewFile();

        // 依次将每个分块文件的内容复制到合并后的文件中
        try (OutputStream outputStream = new FileOutputStream(mergedFile)) {
            for (File chunk : sortedChunks) {
                try (InputStream inputStream = new FileInputStream(chunk)) {
                    IoUtil.copy(inputStream, outputStream);
                }
            }
        }
        sortedChunks.add(chunkDirFile);
        sortedChunks.stream().forEach(FileUtil::del);
        return FileUtil.getAbsolutePath(mergedFile);
    }

    /**
     * 获取分块文件列表
     *
     * @param identifier
     * @param fileId
     * @return
     */
    public static List<FilePart> getFileParts(String identifier, Long fileId) {
        return getFileParts(getChunkDir(), identifier, fileId);
    }

    /**
     * 获取分块文件列表
     *
     * @param chunkDir
     * @param identifier
     * @param fileId
     * @return
     */
    public static List<FilePart> getFileParts(String chunkDir, String identifier, Long fileId) {
        if (!chunkDir.endsWith("/")) {
            chunkDir = chunkDir + "/";
        }
        // 分块文件所在目录
        File chunkDirFile = FileUtil.newFile(chunkDir + identifier);
        File[] chunkFiles = chunkDirFile.listFiles();
        if (chunkFiles == null || chunkFiles.length == 0) {
            throw new RuntimeException("No chunk files found ");
        }
        // 按分块编号排序（假设分块文件名为 "1.part", "2.part", …）
        List<FilePart> fileParts = Arrays.stream(chunkFiles).filter(file -> ("." + FileUtil.getSuffix(file)).endsWith(getPartSuffix())).map(file ->
                new FilePart()
                        .setFileId(fileId)
                        .setLocal(Boolean.TRUE)
                        .setLocalResource(FileUtil.getAbsolutePath(file))
                        .setPartCode(identifier)
                        .setPartSort(Integer.parseInt(FileUtil.mainName(file)))
                        .setPartSize(IoUtils.size(FileUtil.getInputStream(file)))
        ).sorted(Comparator.comparingInt(FilePart::getPartSort)).collect(Collectors.toList());
        return fileParts;
    }

    /**
     * 获取分块文件路径
     *
     * @param identifier
     * @param totalChunks
     * @return
     */
    public static List<InputStream> getSplitFileLocal(String identifier, int totalChunks) {
        List<InputStream> streamList = CollUtil.newArrayList();
        List<FilePart> fileParts = getFileParts(identifier, null);
        streamList.addAll(fileParts.stream().filter(FilePart::getLocal).map(filePart -> FileUtil.getInputStream(filePart.getLocalResource())).collect(Collectors.toList()));
        if (!ObjectUtils.equals(totalChunks, streamList.size())) {
            throw new GlobalCustomException("文件分块数量不匹配");
        }
        return streamList;
    }
}

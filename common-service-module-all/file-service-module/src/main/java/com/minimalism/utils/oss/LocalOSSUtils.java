package com.minimalism.utils.oss;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.file.properties.FileProperties;
import com.minimalism.file.storage.StorageType;
import com.minimalism.utils.object.ObjectUtils;
import lombok.SneakyThrows;
import org.springframework.core.env.Environment;

import java.io.*;
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
    private static final int CHUNK_SIZE = 1024 * 1024; // 每个分片大小为1MB
    // 分块文件存放目录
    private static final String CHUNK_DIR = getUploadDir() + "chunks/";
    // 合并后文件存放目录
    private static final String MERGE_DIR = getUploadDir() + "merged/";

    public static String getUploadDir() {
        String uploadDir = "/";
        //try {
        //    FileProperties fileProperties = SpringUtil.getBean(FileProperties.class);
        //    if (ObjectUtils.equals(fileProperties.getType(), StorageType.local)) {
        //        FileProperties.LocalProperties local = fileProperties.getLocal();
        //        uploadDir = local.getUploadDir();
        //    }
        //} catch (Exception e) {
        //    uploadDir = SpringUtil.getBean(Environment.class).getProperty("file.properties.local.uploadDir", String.class, "tmp/uploads");
        //}
        uploadDir = "tmp/uploads";

        if (!uploadDir.endsWith("/")) {
            uploadDir = uploadDir + "/";
        }
        return uploadDir;
    }


    /**
     * 将指定文件拆分成若干个分块文件
     * @param chunkDir 分块文件存放目录
     * @param fileName 原始文件名
     * @param identifier 唯一值
     * @param input 原始文件输入流
     */
    @SneakyThrows
    public static void splitFileLocal(String chunkDir, String fileName, String identifier, InputStream input) {
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
            byte[] buffer = new byte[CHUNK_SIZE];
            int bytesRead;
            // 循环读取，直到返回 -1 表示读完
            while ((bytesRead = fis.read(buffer)) != -1) {
                // 每个分块文件命名为 “chunkNumber.part”
                File tempFile = FileUtil.newFile(chunkDirPath + "/" + chunkNumber + ".part");
                // 写入读取的字节到分块文件中
                try (OutputStream os = new FileOutputStream(tempFile)) {
                    os.write(buffer, 0, bytesRead);
                }
                chunkNumber++;
            }
        }
    }

    /**
     * 将分块文件合并为一个完整文件，并清理临时分块文件
     * @param chunkDir 分片文件夹(不包含唯一值)
     * @param mergeDir 合并文件夹
     * @param fileName 原始文件名
     * @param identifier 唯一值
     */
    @SneakyThrows
    public static void mergeFileLocal(String chunkDir, String mergeDir, String fileName, String identifier) {
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
    }

    public static void main(String[] args) {
        System.out.printf(" %s", getUploadDir());
    }
}

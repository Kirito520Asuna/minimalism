package com.minimalism.utils.io;

import cn.hutool.core.io.IoUtil;
import com.minimalism.exception.GlobalCustomException;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author yan
 * @Date 2025/3/7 21:07:13
 * @Description
 */
public class IoUtils extends IoUtil {
    @SneakyThrows
    public static long size(InputStream inputStream) {
        long size = 0;
        if (inputStream != null) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                size += bytesRead;
            }
        }
        return size;
    }

    @SneakyThrows
    public static byte[] toByteArray(InputStream in) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        return baos.toByteArray();
    }

    @SneakyThrows
    public static byte[] toByteArray(List<InputStream> inList) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (InputStream in : inList) {
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }
            } finally {
                // 确保每个流在读取后被关闭
                close(in);
            }
        }
        return baos.toByteArray();
    }

    /**
     * 将输入流按指定大小分割成多个输入流
     * @param in
     * @param chunkSize
     * @return
     */
    @SneakyThrows
    public static List<InputStream> splitInputStream(InputStream in, int chunkSize) {
        List<InputStream> result = new ArrayList<>();
        byte[] buffer = new byte[chunkSize];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            // 如果最后一次读取的字节数不足 chunkSize，复制有效数据
            byte[] chunkData = (bytesRead == chunkSize) ? buffer.clone() : Arrays.copyOf(buffer, bytesRead);
            result.add(new ByteArrayInputStream(chunkData));
        }
        return result;
    }

    /**
     * 合并多个输入流
     * @param inputStreams
     * @return
     */
    @SneakyThrows
    public static ByteArrayInputStream mergeInputStream(List<InputStream> inputStreams) {
       return new ByteArrayInputStream(toByteArray(inputStreams));
    }

    /**
     *
     * @param list
     * @return
     */
    @SneakyThrows
    public static ByteArrayOutputStream merge(List<InputStream> list) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ByteArrayOutputStream outputStream = out) {
            for (InputStream chunk : list) {
                try (InputStream inputStream = chunk) {
                    IoUtils.copy(inputStream, outputStream);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new GlobalCustomException("合并错误！");
        }
        return out;
    }
}

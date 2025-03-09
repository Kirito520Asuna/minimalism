package com.minimalism.utils.io;

import cn.hutool.core.io.IoUtil;
import com.minimalism.exception.GlobalCustomException;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
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
    public static byte[] toByteArray(InputStream inputStream) {
        return IOUtils.toByteArray(inputStream);
    }

    @SneakyThrows
    public static byte[] toByteArray(InputStream input, int size) {
        return IOUtils.toByteArray(input, size);
    }

    @SneakyThrows
    public static byte[] toByteArray(InputStream input, long size) {
        return IOUtils.toByteArray(input, size);
    }

    @SneakyThrows
    public static byte[] toByteArray(Reader reader) {
        return IOUtils.toByteArray(reader);
    }

    @SneakyThrows
    public static byte[] toByteArray(Reader reader, Charset charset) {
        return IOUtils.toByteArray(reader, charset);
    }

    @SneakyThrows
    public static byte[] toByteArray(Reader reader, String charsetName) {
        return IOUtils.toByteArray(reader, charsetName);
    }

    @SneakyThrows
    public static byte[] toByteArray(String input) {
        return IOUtils.toByteArray(input);
    }

    @SneakyThrows
    public static byte[] toByteArray(URI uri) throws IOException {
        return IOUtils.toByteArray(uri);
    }

    @SneakyThrows
    public static byte[] toByteArray(URL url) {
        return IOUtils.toByteArray(url);

    }

    @SneakyThrows
    public static byte[] toByteArray(URLConnection urlConn) {
        return IOUtils.toByteArray(urlConn);
    }

    @SneakyThrows
    public static byte[] toByteArray(List<InputStream> inList) {
        return merge(inList).toByteArray();
    }

    /**
     * 将输入流按指定大小分割成多个输入流
     *
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
     *
     * @param inputStreams
     * @return
     */
    @SneakyThrows
    public static ByteArrayInputStream mergeInputStream(List<InputStream> inputStreams) {
        return new ByteArrayInputStream(toByteArray(inputStreams));
    }

    /**
     * 内存不足会出现 java heap space 异常
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
                    copy(inputStream, outputStream);
                } finally {
                    close(chunk);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new GlobalCustomException("合并错误！");
        }
        return out;
    }
    @SneakyThrows
    public static long getInputStreamLength(byte[] bytes) {
        return getInputStreamLength(new ByteArrayInputStream(bytes));
    }
    @SneakyThrows
    public static long getInputStreamLength(InputStream inputStream) {
        byte[] buffer = new byte[1024];
        long length = 0;
        int bytesRead;
        // 防止流耗尽
        byte[] bytes = toByteArray(inputStream);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        while ((bytesRead = byteArrayInputStream.read(buffer)) != -1) {
            length += bytesRead;
        }
        return length;
    }
}

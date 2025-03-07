package com.minimalism.utils.io;

import cn.hutool.core.io.IoUtil;
import lombok.SneakyThrows;

import java.io.InputStream;

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
}

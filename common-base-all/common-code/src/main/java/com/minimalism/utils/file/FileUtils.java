package com.minimalism.utils.file;

import cn.hutool.core.io.FileUtil;
import com.minimalism.constant.Constants;

import java.io.File;

/**
 * @Author yan
 * @Date 2025/3/12 22:36:32
 * @Description
 */
public class FileUtils extends FileUtil {
    public static boolean isFile(String path) {
        return FileUtil.isFile(path) || FileUtil.getName(path).endsWith(Constants.PART_SUFFIX);
    }

    /**
     * 判断是否为文件，如果file为null，则返回false
     *
     * @param file 文件
     * @return 如果为文件true
     */
    public static boolean isFile(File file) {
        return FileUtil.isFile(file) || FileUtil.getName(file).contains(Constants.PART_SUFFIX);
    }
}

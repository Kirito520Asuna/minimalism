package com.minimalism.file.storage;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.file.domain.FileInfo;
import com.minimalism.utils.io.IoUtils;
import com.minimalism.utils.object.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * @Author yan
 * @Date 2025/3/7 18:58:37
 * @Description
 */
public interface IFileStorageClient extends AbstractBean {
    default StorageType getType() {
        return StorageType.local;
    }
    default String getBucket() {return null;}
    default String getEndPoint() {return null;}
    /**
     * 判断存储桶是否在存在
     *
     * @param bucket 桶名称
     * @return true 存在 false 不存在
     */
    boolean bucketExists(String bucket);

    /**
     * 创建存储桶
     *
     * @param bucket 桶名称
     */
    void makeBucket(String bucket);

    /**
     * 删除文件
     *
     * @param objectName 对象名称
     */
    default void delete(String objectName) {
        delete(null, objectName);
    }

    /**
     * 删除文件
     *
     * @param bucketName
     * @param objectName
     */
    default void delete(String bucketName, String objectName) {
        if (StrUtil.isBlank(bucketName)) {
            bucketName = getBucket();
        }
    }

    /**
     * 下载文件
     *
     * @param objectName 对象名称
     * @param response
     */
    default void download(String objectName, HttpServletResponse response) {
        download(null, objectName, response);
    }

    /**
     * 下载文件
     *
     * @param bucketName
     * @param objectName
     * @param response
     */
    default void download(String bucketName, String objectName, HttpServletResponse response) {
        if (StrUtil.isBlank(bucketName)) {
            bucketName = getBucket();
        }
    }

    /**
     * 构建文件信息
     *
     * @param flieName
     * @param inputStream
     * @param url
     * @param dir
     * @param img
     * @return
     */
    default FileInfo buildFileInfo(String flieName, InputStream inputStream, String url, Boolean local,Boolean dir, Boolean img) {
        String mainName = FileUtil.mainName(flieName);
        String type = FileUtil.extName(flieName);
        long size = IoUtils.size(inputStream);

        return new FileInfo()
                .setUrl(url)
                .setDir(dir)
                .setFileName(mainName)
                .setImg(img)
                .setLocal(ObjectUtils.defaultIfEmpty(local,Boolean.FALSE))
                .setSuffix("." + type)
                .setType(type)
                .setName(mainName)
                .setSize(size);
    }

    /**
     * 上传文件
     *
     * @param flieName
     * @param inputStream
     */
    default FileInfo upload(String flieName, InputStream inputStream) {
        return upload(null, flieName, inputStream);
    }


    /**
     * 上传文件
     *
     * @param bucketName  桶名称
     * @param flieName    文件名称
     * @param inputStream
     */
    default FileInfo upload(String bucketName, String flieName, InputStream inputStream) {
        if (StrUtil.isBlank(bucketName)) {
            bucketName = getBucket();
        }
        return null;
    }

    default FileInfo uploadSharding(String flieName, InputStream inputStream) {
        return uploadSharding(null, flieName, inputStream);
    }

    /**
     * 上传文件 分片
     *
     * @param bucketName
     * @param flieName
     * @param inputStream
     */
    default FileInfo uploadSharding(String bucketName, String flieName, InputStream inputStream) {
        if (StrUtil.isBlank(bucketName)) {
            bucketName = getBucket();
        }
        return null;
    }

    /**
     * 获取url
     * @param objectName
     * @return
     */
    default String getUrl(String objectName) {
        return getUrl(null, objectName);
    }

    /**
     * 获取url
     * @param bucketName
     * @param objectName
     * @return
     */
    default String getUrl(String bucketName, String objectName) {
        if (StrUtil.isBlank(bucketName)) {
            bucketName = getBucket();
        }
        String url = "https://" + bucketName + "." + getEndPoint() + "/" + objectName;
        return url.replace("//", "/").replace(":/", "://");
    }

}

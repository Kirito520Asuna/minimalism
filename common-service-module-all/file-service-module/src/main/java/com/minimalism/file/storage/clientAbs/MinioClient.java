package com.minimalism.file.storage.clientAbs;

import cn.hutool.core.collection.CollUtil;
import com.minimalism.file.storage.IFileStorageClient;
import com.minimalism.file.storage.StorageType;

import java.util.List;

/**
 * @Author yan
 * @Date 2025/3/14 2:17:21
 * @Description
 */
public interface MinioClient extends IFileStorageClient {
    List<StorageType> minioClientList = CollUtil.newArrayList(StorageType.minio);

    @Override
    default boolean support(StorageType storageType) {
        return minioClientList.contains(storageType);
    }
}

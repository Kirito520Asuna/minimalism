package com.minimalism.file.storage.clientAbs;

import cn.hutool.core.collection.CollUtil;
import com.minimalism.file.storage.IFileStorageClient;
import com.minimalism.file.storage.StorageType;

import java.util.List;

/**
 * @Author yan
 * @Date 2025/3/8 16:40:36
 * @Description
 */
public interface AliyunClient extends IFileStorageClient {
    List<StorageType> aliyunClientList = CollUtil.newArrayList(StorageType.aliyunOSS);

    @Override
    default boolean support(StorageType storageType) {
        return aliyunClientList.contains(storageType);
    }
}

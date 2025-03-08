package com.minimalism.file.storage.clientAbs;

import cn.hutool.core.collection.CollUtil;
import com.minimalism.file.storage.IFileStorageClient;
import com.minimalism.file.storage.StorageType;

import java.util.List;

/**
 * @Author yan
 * @Date 2025/3/8 16:37:37
 * @Description
 */
public interface LocalClient extends IFileStorageClient {
    List<StorageType> localClientList = CollUtil.newArrayList(StorageType.local);

    @Override
    default boolean support(StorageType storageType) {
        return localClientList.contains(storageType);
    }

}

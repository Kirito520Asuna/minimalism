package com.minimalism.file.storage;

import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.exception.BusinessException;
import com.minimalism.file.properties.FileProperties;
import com.minimalism.file.storage.platform.AliyunOssStorageClient;
import com.minimalism.file.storage.platform.LocalStorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @Author yan
 * @Date 2025/3/7 19:57:48
 * @Description
 */
@Component @Deprecated
@RequiredArgsConstructor
public class IStorageFactory implements IFileStorageProvider, AbstractBean {
    @Override
    public void init() {
        debug("[init]-[Factory] {}",getAClass().getName());
    }

    @Override
    public IFileStorageClient getStorage() {
        FileProperties fileProperties = SpringUtil.getBean(FileProperties.class);
        StorageType type = fileProperties.getType();
        IFileStorageClient fileStorage;
        switch (type) {
            case aliyunOSS: fileStorage = new AliyunOssStorageClient(fileProperties.getAliyunOss()); break;
            case local: fileStorage = new LocalStorageClient(fileProperties.getLocal()); break;
            default: throw new BusinessException("不支持的存储平台");
        }
        return fileStorage;
    }
}

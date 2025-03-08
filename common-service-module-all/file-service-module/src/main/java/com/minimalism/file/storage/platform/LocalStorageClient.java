package com.minimalism.file.storage.platform;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.exception.BusinessException;
import com.minimalism.exception.GlobalConfigException;
import com.minimalism.file.domain.FileInfo;
import com.minimalism.file.properties.FileProperties;
import com.minimalism.file.storage.IFileStorageClient;
import com.minimalism.file.storage.StorageType;
import com.minimalism.utils.io.IoUtils;
import com.minimalism.utils.object.ObjectUtils;
import com.minimalism.utils.oss.LocalOSSUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * @Author yan
 * @Date 2025/3/7 20:44:58
 * @Description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocalStorageClient implements IFileStorageClient {
    private String directory;
    private String endPoint;
    private String nginxUrl;
    private String uploadDir;

    public LocalStorageClient(FileProperties.LocalProperties config) {
        try {
            String directory = config.getDirectory();
            String endPoint = config.getEndPoint();
            String nginxUrl = config.getNginxUrl();
            String uploadDir = config.getUploadDir();
            uploadDir = ObjectUtils.defaultIfEmpty(uploadDir, "tmp/upload");
            if (!uploadDir.endsWith("/")) {
                uploadDir = uploadDir + "/";
            }
            this.directory = directory;
            this.endPoint = endPoint;
            this.nginxUrl = nginxUrl;
            this.uploadDir = uploadDir;
        } catch (Exception e) {
            error("[Local] LocalStorage build failed: {}", e.getMessage());
            throw new GlobalConfigException("请检查本地存储配置是否正确");
        }
    }

    @Override
    public StorageType getType() {
        return StorageType.local;
    }

    @Override
    public boolean bucketExists(String bucket) {
        return FileUtil.newFile(bucket).exists();
    }

    @Override
    public void makeBucket(String bucket) {
        try {
            if (!bucketExists(directory)) {
                FileUtil.newFile(bucket).mkdirs();
            }
        } catch (Exception e) {
            error("[Local] makeBucket Exception:{}", e.getMessage());
            throw new GlobalConfigException("创建存储桶失败");
        }
    }

    @Override
    public FileInfo upload(String bucketName, String flieName, InputStream inputStream) {
        IFileStorageClient.super.upload(bucketName, flieName, inputStream);
        String path = uploadDir + "/" + bucketName + "/" + flieName;
        path = path.replace("//", "/");

        IoUtils.copy(inputStream, FileUtil.getOutputStream(FileUtil.newFile(path)));

        String url = getUrl(bucketName, flieName);
        Boolean aFalse = Boolean.FALSE;
        return buildFileInfo(flieName, inputStream, url, aFalse, aFalse);
    }

    @Override
    public FileInfo uploadSharding(String bucketName, String flieName, InputStream inputStream) {
        IFileStorageClient.super.uploadSharding(bucketName, flieName, inputStream);
        String identifier = UUID.randomUUID().toString().replace("-", "") + "_" + flieName;
        LocalOSSUtils.splitFileLocal(flieName,identifier,inputStream);
        String fileLocal = LocalOSSUtils.mergeFileLocal(flieName, identifier);
        Boolean aFalse = Boolean.FALSE;
        return buildFileInfo(flieName, inputStream, fileLocal, aFalse, aFalse);
    }

    @Override
    public void delete(String bucketName, String objectName) {
        IFileStorageClient.super.delete(bucketName, objectName);
        if (StringUtils.isEmpty(objectName)) {
            throw new BusinessException("文件删除失败,文件路径为空");
        }
        try {
            Path file = Paths.get(getUploadDir()).resolve(Paths.get(objectName));
            FileUtil.del(file);
        } catch (Exception e) {
            error("[Local] file delete failed: {}", e.getMessage());
            throw new BusinessException("文件删除失败");
        }
    }

    @Override
    public String getUrl(String bucketName, String objectName) {
        IFileStorageClient.super.getUrl(bucketName, objectName);
        String url;
        // 如果配置了nginxUrl则使用nginxUrl
        if (StrUtil.isNotBlank(nginxUrl)) {
            url = nginxUrl.endsWith("/") ? nginxUrl + bucketName + "/" + objectName : nginxUrl + "/" + bucketName + "/" + objectName;
        } else {
            url = endPoint + "/" + LocalOSSUtils.getUploadDir() + "/" + bucketName + "/" + objectName;
        }
        url = url.replace("//", "/").replace(":/", "://");
        return url;
    }


}

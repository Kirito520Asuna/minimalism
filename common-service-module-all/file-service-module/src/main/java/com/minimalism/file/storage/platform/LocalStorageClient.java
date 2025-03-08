package com.minimalism.file.storage.platform;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.minimalism.exception.BusinessException;
import com.minimalism.exception.GlobalConfigException;
import com.minimalism.file.domain.FileInfo;
import com.minimalism.file.domain.FilePart;
import com.minimalism.file.properties.FileProperties;
import com.minimalism.file.storage.StorageType;
import com.minimalism.file.storage.clientAbs.LocalClient;
import com.minimalism.utils.object.ObjectUtils;
import com.minimalism.utils.oss.LocalOSSUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/**
 * @Author yan
 * @Date 2025/3/7 20:44:58
 * @Description
 */
@Service
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocalStorageClient implements LocalClient {
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
    public void delete(String bucketName, String objectName) {
        LocalClient.super.delete(bucketName, objectName);
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
    public FileInfo upload(String bucketName, String flieName, InputStream inputStream) {
        LocalClient.super.upload(bucketName, flieName, inputStream);
        String uploadUrl = LocalOSSUtils.upload(flieName, inputStream);
        Boolean aFalse = Boolean.FALSE;
        return buildFileInfo(flieName, FileUtil.getInputStream(uploadUrl), uploadUrl, Boolean.TRUE, aFalse, aFalse);
    }

    @Override
    public FileInfo uploadSharding(String bucketName, String flieName, InputStream inputStream) {
        LocalClient.super.uploadSharding(bucketName, flieName, inputStream);
        String bucketPath = uploadDir + "/" + bucketName + "/";
        bucketPath = bucketPath.replace("//", "/");
        File bucketFile = FileUtil.newFile(bucketPath);
        if (!bucketFile.exists()) {
            bucketFile.mkdirs();
        }
        String identifier = UUID.randomUUID().toString().replace("-", "") + "_" + flieName;
        String fileLocal = LocalOSSUtils.uploadSharding(bucketName, identifier, inputStream);
        Boolean aFalse = Boolean.FALSE;
        return buildFileInfo(flieName, FileUtil.getInputStream(fileLocal), fileLocal, Boolean.TRUE, aFalse, aFalse);
    }

    @Override
    public FilePart uploadShardingChunkNumber(String bucketName, int chunkNumber, String identifier, InputStream inputStream) {
        LocalClient.super.uploadShardingChunkNumber(bucketName, chunkNumber, identifier, inputStream);
        String partFileLocalUrl = LocalOSSUtils.splitChunkNumberFileLocal(chunkNumber, identifier, inputStream);
        return bulidFilePart(identifier, chunkNumber, partFileLocalUrl, Boolean.TRUE, FileUtil.getInputStream(partFileLocalUrl));
    }


    @Override
    public List<InputStream> getInputStreams(String bucketName, String identifier, int totalChunks) {
        List<InputStream> list = LocalOSSUtils.getSplitFileLocal(identifier, totalChunks);
        return list;
    }


    @Override
    public String getUrl(String bucketName, String objectName) {
        LocalClient.super.getUrl(bucketName, objectName);
        String url;
        // 如果配置了nginxUrl则使用nginxUrl
        if (StrUtil.isNotBlank(nginxUrl)) {
            url = nginxUrl.endsWith("/") ? nginxUrl + bucketName + "/" + objectName : nginxUrl + "/" + bucketName + "/" + objectName;
        } else if (StrUtil.isBlank(endPoint)) {
            //表示直接使用服务器地址
            url = "";
        } else {
            url = endPoint + "/" + LocalOSSUtils.getUploadDir() + "/" + bucketName + "/" + objectName;
        }
        url = url.replace("//", "/").replace(":/", "://");
        return url;
    }

}

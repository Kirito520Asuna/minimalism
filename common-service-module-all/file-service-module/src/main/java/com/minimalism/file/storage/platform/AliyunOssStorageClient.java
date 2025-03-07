package com.minimalism.file.storage.platform;

import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.minimalism.exception.GlobalConfigException;
import com.minimalism.file.domain.FileInfo;
import com.minimalism.file.properties.FileProperties;
import com.minimalism.file.storage.IFileStorageClient;
import com.minimalism.file.storage.StorageType;
import com.minimalism.utils.io.IoUtils;
import com.minimalism.utils.oss.AliyunOSSUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


/**
 * @Author yan
 * @Date 2025/3/7 19:24:20
 * @Description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AliyunOssStorageClient implements IFileStorageClient {
    private OSS client;
    private String endPoint;
    private String bucket;

    public AliyunOssStorageClient(FileProperties.AliyunOssProperties config) {
        try {
            String accessKey = config.getAccessKey();
            String secretKey = config.getSecretKey();
            String endPoint = config.getEndpoint();
            String bucket = config.getBucket();
            this.client = new OSSClientBuilder().build(endPoint, accessKey, secretKey);
            this.endPoint = endPoint;
            this.bucket = bucket;
        } catch (Exception e) {
            error("[AliyunOSS] OSSClient build failed: {}", e.getMessage());
            throw new GlobalConfigException("请检查阿里云OSS配置是否正确");
        }
    }

    @Override
    public StorageType getType() {
        return StorageType.aliyunOSS;
    }

    @Override
    public boolean bucketExists(String bucket) {
        return AliyunOSSUtils.doesBucketExist(client, bucket);
    }

    @Override
    public void makeBucket(String bucket) {
        AliyunOSSUtils.createBucketName(client, bucket);
    }

    @Override
    public void delete(String bucketName, String objectName) {
        if (StrUtil.isBlank(bucketName)) {
            bucketName = bucket;
        }
        AliyunOSSUtils.deleteOss(client, bucketName, objectName);
    }

    @SneakyThrows
    @Override
    public void download(String bucketName, String objectName, HttpServletResponse response) {
        if (StrUtil.isBlank(bucketName)) {
            bucketName = getBucket();
        }
        InputStream inputStream = AliyunOSSUtils.downloadOssInput(client, bucketName, objectName);
        OutputStream responseOutputStream = response.getOutputStream();
        // 将 OSS 输出流的内容复制到 HTTP 响应输出流中
        IoUtils.copy(inputStream, responseOutputStream);
        // 设置文件ContentType类型，这样设置，会自动判断下载文件类型
        response.setContentType("application/x-msdownload");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(objectName, String.valueOf(StandardCharsets.UTF_8)));
        response.flushBuffer();
        IoUtils.close(responseOutputStream);
    }

    @Override
    public FileInfo upload(String bucketName, String flieName, InputStream inputStream) {
        IFileStorageClient.super.upload(bucketName, flieName, inputStream);
        String url = AliyunOSSUtils.uploadOss(client, bucketName, flieName, inputStream);
        Boolean aFalse = Boolean.FALSE;
        return buildFileInfo(flieName, inputStream, url,aFalse,aFalse);
    }

    @SneakyThrows
    @Override
    public FileInfo uploadSharding(String bucketName, String flieName, InputStream inputStream) {
        IFileStorageClient.super.uploadSharding(bucketName, flieName, inputStream);
        String url = AliyunOSSUtils.uploadShardingOss(client, bucketName, flieName, inputStream);

        Boolean aFalse = Boolean.FALSE;
        return buildFileInfo(flieName, inputStream, url,aFalse,aFalse);
    }


}

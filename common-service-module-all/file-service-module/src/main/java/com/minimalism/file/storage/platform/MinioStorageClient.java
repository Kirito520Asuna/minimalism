package com.minimalism.file.storage.platform;

import cn.hutool.core.util.StrUtil;
import com.minimalism.exception.GlobalConfigException;
import com.minimalism.file.domain.FileInfo;
import com.minimalism.file.domain.FilePart;
import com.minimalism.file.properties.FileProperties;
import com.minimalism.file.storage.StorageType;
import com.minimalism.file.storage.clientAbs.AliyunClient;
import com.minimalism.utils.io.IoUtils;
import com.minimalism.utils.oss.AliyunOSSUtils;
import com.minimalism.utils.oss.MinioOSSUtils;
import io.minio.MinioClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


/**
 * Minio文件上传
 *
 * @Author: hao.ding@insentek.com
 * @Date: 2024/1/25 10:05
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MinioStorageClient implements com.minimalism.file.storage.clientAbs.MinioClient {

    private MinioClient minioClient;
    private String endPoint;
    private String bucket;

    public void init(FileProperties.MinioProperties config) {
        try {
            String accessKey = config.getAccessKey();
            String secretKey = config.getSecretKey();
            String endPoint = config.getEndpoint();
            String bucket = config.getBucket();
            this.minioClient = MinioOSSUtils.createMinioClient(accessKey, secretKey, endPoint);
            this.endPoint = endPoint;
            this.bucket = bucket;
        } catch (Exception e) {
            log.error("[Minio] MinioClient build failed: {}", e.getMessage());
            throw new GlobalConfigException("请检查Minio配置是否正确");
        }
    }

    public MinioStorageClient(FileProperties.MinioProperties config) {
        init(config);
    }
    @Override
    public StorageType getType() {
        return StorageType.minio;
    }
    @Override
    public boolean bucketExists(String bucket) {
        return MinioOSSUtils.doesBucketExist(minioClient, bucket);
    }

    @Override
    public void makeBucket(String bucket) {
        MinioOSSUtils.createBucket(minioClient, bucket);
    }

    @Override
    public void delete(String bucketName, String objectName) {
        MinioOSSUtils.deleteMinio(minioClient, bucketName, objectName);
    }
    @SneakyThrows
    @Override
    public void download(String bucketName, String objectName, HttpServletResponse response) {
        if (StrUtil.isBlank(bucketName)) {
            bucketName = getBucket();
        }
        InputStream inputStream = MinioOSSUtils.downloadMinioInput(minioClient, bucketName, objectName);
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
        com.minimalism.file.storage.clientAbs.MinioClient.super.upload(bucketName, flieName, inputStream);
        String url = MinioOSSUtils.uploadMinio(minioClient, bucketName, flieName, inputStream);
        Boolean aFalse = Boolean.FALSE;
        return buildFileInfo(flieName, inputStream, url, aFalse, aFalse, aFalse);
    }

    @SneakyThrows
    @Override
    public FileInfo uploadSharding(String bucketName, String flieName, InputStream inputStream, String identifier) {
        com.minimalism.file.storage.clientAbs.MinioClient.super.uploadSharding(bucketName, flieName, inputStream, identifier);
        String url = MinioOSSUtils.uploadShardingOss(minioClient, bucketName, flieName, inputStream);

        Boolean aFalse = Boolean.FALSE;
        return buildFileInfo(flieName, inputStream, url, aFalse, aFalse, aFalse);
    }

    @Override
    public FilePart uploadShardingChunkNumber(String bucketName, int chunkNumber, String identifier, InputStream inputStream) {
        //todo
        throw new UnsupportedOperationException("Minio暂不支持单个分片上传");
    }

    @Override
    public FileInfo uploadMergeChunks(InputStream inputStream, String fileMainName, String identifier) {
        //todo
        throw new UnsupportedOperationException("Minio暂不支持单独操作分片合并上传");
    }
}


package com.minimalism.file.properties;


import com.minimalism.abstractinterface.service.properties.BeanProperties;
import com.minimalism.config.OSConfig;
import com.minimalism.enums.OSType;
import com.minimalism.file.storage.StorageType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * fs上传配置资源类
 *
 * @Author yan
 * @Date 2025/3/7 下午6:06:55
 * @Description
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "file.properties")
@Configuration
public class FileProperties implements BeanProperties {

    /**
     * 上传类型-默认local
     */
    private StorageType type = StorageType.local;

    /**
     * 本地上传配置
     */
    private LocalProperties local = new LocalProperties();

    /**
     * 七牛配置
     */
    private QiniuProperties qiniu;

    /**
     * oss配置
     */
    private AliyunOssProperties aliyunOss;

    /**
     * minio配置
     */
    private MinioProperties minio;


    @Data
    public static class LocalProperties {
        private String directory = "local";
        private String endPoint = "";
        private String nginxUrl = "";
        private String nginxDir = "";
        private String uploadDir = "tmp/uploads";

        public String getUploadDir() {
            String separator = OSConfig.separator;
            return uploadDir.endsWith(separator) ? uploadDir : uploadDir + separator;
        }
    }

    @Data
    public static class AliyunOssProperties {

        private String accessKey;

        private String secretKey;

        private String endpoint;

        private String bucket;
    }

    @Data
    public static class QiniuProperties {

        private String accessKey;

        private String secretKey;

        private String bucket;

        private String path;
    }

    @Data
    public static class MinioProperties {

        private String accessKey;

        private String secretKey;

        private String endpoint;

        private String bucket;
    }
}

package com.minimalism.file.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.constant.file.FileConstant;
import com.minimalism.file.properties.FileProperties;
import com.minimalism.file.storage.clientAbs.LocalClient;
import com.minimalism.utils.NacosUtils;
import com.minimalism.utils.oss.LocalOSSUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.unit.DataSize;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.MultipartConfigElement;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2025/3/9 1:57:11
 * @Description
 */
@Configuration
public class FileUploadConfig implements AbstractBean {
    public static String instanceId;
    public static String fileControllerPath = "/file";
    public static String getPathKey = "file.byte.get";
    public static String delPathKey = "file.byte.del";

    public static String getInstanceId() {
        if (StrUtil.isBlank(instanceId)||instanceId.endsWith("-1")){
            instanceId = NacosUtils.getInstanceId();
        }
        return instanceId;
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        Environment env = SpringUtil.getBean(Environment.class);
        Integer maxFileSize = env.getProperty("file.upload.maxFileSize", Integer.class, 10);
        Integer maxRequestSize = env.getProperty("file.upload.maxRequestSize", Integer.class, 1024);
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 设置单个文件的最大值
        factory.setMaxFileSize(DataSize.ofMegabytes(maxFileSize)); // 10MB
        // 设置总上传数据的最大值
        factory.setMaxRequestSize(DataSize.ofMegabytes(maxRequestSize)); // 10MB
        return factory.createMultipartConfig();
    }

    @Override
    @PostConstruct
    public void init() {
        AbstractBean.super.init();
        List<String> fileNameList = LocalOSSUtils.getFileNameList();
        instanceId = NacosUtils.getInstanceId();
        RedisTemplate redisTemplate = SpringUtil.getBean(RedisTemplate.class);
        redisTemplate.opsForHash().put(FileConstant.FILE_REDIS_MAPPER, getInstanceId(), fileNameList);
        LocalOSSUtils.FILE_NAME_LIST.addAll(fileNameList);
        fileNameList.stream().forEach(fileName -> {
            redisTemplate.opsForHash().put(FileConstant.REDIS_FILE_INSTANCE_ID, fileName, getInstanceId());
        });
    }


    @Override
    @PreDestroy
    public void destroy() {
        RedisTemplate redisTemplate = SpringUtil.getBean(RedisTemplate.class);
        redisTemplate.opsForHash().delete(FileConstant.FILE_REDIS_MAPPER, getInstanceId());
        LocalOSSUtils.FILE_NAME_LIST.stream().forEach(fileName -> {
            redisTemplate.opsForHash().delete(FileConstant.REDIS_FILE_INSTANCE_ID, fileName);
        });
        AbstractBean.super.destroy();
    }

    /**
     * 获取其他服务器文件字节 的url
     *
     * @param instanceId
     * @return
     */
    public static String getUrlByte(String instanceId) {
        return getUrl(instanceId, FileUploadConfig.getPathKey);
    }

    /**
     * 获取其他服务器文件删除的url
     *
     * @param instanceId
     * @return
     */
    public static String getUrlDel(String instanceId) {
        return getUrl(instanceId, FileUploadConfig.delPathKey);
    }

    public static String getUrl(String instanceId, String path) {
        Environment environment = SpringUtil.getBean(Environment.class);
        String prefix = environment.getProperty("server.servlet.context-path", "");
        String serviceId = environment.getProperty("spring.application.name", "");
        prefix = prefix + fileControllerPath;
        return NacosUtils.getUrl(serviceId, instanceId, prefix, path);
    }
}

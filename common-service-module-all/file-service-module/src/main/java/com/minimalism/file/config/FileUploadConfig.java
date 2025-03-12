package com.minimalism.file.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.constant.file.FileConstant;
import com.minimalism.utils.NacosUtils;
import com.minimalism.utils.oss.LocalOSSUtils;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.unit.DataSize;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.servlet.MultipartConfigElement;
import java.util.List;

/**
 * @Author yan
 * @Date 2025/3/9 1:57:11
 * @Description
 */
@AutoConfigureAfter(NacosDiscoveryProperties.class)
@Configuration @ConditionalOnBean(RedisTemplate.class)
public class FileUploadConfig implements AbstractBean {
    public static String instanceId;
    public static String fileControllerPath = "/file";
    public static String getPathKey = "file.byte.get";
    public static String delPathKey = "file.byte.del";

    @Resource
    private RedisTemplate redisTemplate;
    public static String getInstanceId() {
        if (StrUtil.isBlank(instanceId) || instanceId.endsWith("-1")) {
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
        this.instanceId = NacosUtils.getInstanceId();
        RedisTemplate redisTemplate = SpringUtil.getBean(RedisTemplate.class);
        String instanceId = getInstanceId();
        LocalOSSUtils.FILE_NAME_LIST.addAll(fileNameList);
        LocalOSSUtils.FILE_NAME_LIST.stream().forEach(fileName -> {
            if (FileUtil.isFile(fileName)) {
                redisTemplate.opsForHash().put(FileConstant.FILE_REDIS_FILE, fileName, instanceId);
            } else if (FileUtil.isDirectory(fileName)) {
                redisTemplate.opsForHash().put(FileConstant.FILE_REDIS_DIR + instanceId, fileName, "DIR");
                redisTemplate.opsForHash().put(FileConstant.FILE_REDIS_INSTANCE_ID, fileName, instanceId);
            }
        });
    }
    @Override
    @PreDestroy
    public void destroy() {
        RedisTemplate redisTemplate = SpringUtil.getBean(RedisTemplate.class);
        //redisTemplate.opsForHash().delete(FileConstant.FILE_REDIS_DIR + getInstanceId());
        LocalOSSUtils.FILE_NAME_LIST.stream().forEach(fileName -> {
            if (FileUtil.isFile(fileName)) {
                redisTemplate.opsForHash().delete(FileConstant.FILE_REDIS_FILE, fileName);
            } else if (FileUtil.isDirectory(fileName)) {
                redisTemplate.opsForHash().delete(FileConstant.FILE_REDIS_DIR + instanceId, fileName);
                redisTemplate.opsForHash().delete(FileConstant.FILE_REDIS_INSTANCE_ID, fileName);
            }
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
        return getUrl(instanceId, SpringUtil.getBean(Environment.class).getProperty(getPathKey));
    }

    /**
     * 获取其他服务器文件删除的url
     *
     * @param instanceId
     * @return
     */
    public static String getUrlDel(String instanceId) {
        return getUrl(instanceId, SpringUtil.getBean(Environment.class).getProperty(delPathKey));
    }

    public static String getUrl(String instanceId, String path) {
        Environment environment = SpringUtil.getBean(Environment.class);
        String prefix = environment.getProperty("server.servlet.context-path", "");
        String serviceId = environment.getProperty("spring.application.name", "");
        prefix = prefix + fileControllerPath;
        return NacosUtils.getUrl(serviceId, instanceId, prefix, path);
    }
}

package com.minimalism.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.discovery.NacosDiscoveryClient;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.env.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2025/2/7 11:36:35
 * @Description
 */
public class NacosUtils {
    /**
     * @param serviceId  ==> 服务名
     * @param instanceId ==> ip:port
     * @param prefix     ==> 前缀
     * @param path       ==> 路径
     * @return
     */
    public static String getUrl(String serviceId, String instanceId, String prefix, String path) {
        String urlFormat = "http://%s:%s/" + prefix + "/" + path;
        urlFormat = urlFormat.replace("//", "/").replace("//", "/").replace(":/", "://");
        String url = null;
        NacosDiscoveryClient discoveryClient = SpringUtil.getBean(NacosDiscoveryClient.class);

        List<ServiceInstance> serviceInstances = discoveryClient.getInstances(serviceId);
        ServiceInstance serviceInstance = serviceInstances.stream()
                .filter(instance -> ObjectUtil.isNotEmpty(instanceId) || ObjectUtil.equals(instanceId, instance.getInstanceId()))
                .findFirst().orElse(null);
        if (serviceInstance != null) {
            String host = serviceInstance.getHost();
            int port = serviceInstance.getPort();
            url = String.format(urlFormat, host, port);
        } else {
            throw new RuntimeException("服务未找到,请检查服务名是否正确或实例是否在线,服务名:" + serviceId + ",实例id:" + instanceId);
        }
        return url;
    }

    /**
     * 获取当前实例id
     *
     * @return
     */

    public static String getInstanceId() {
        NacosDiscoveryProperties nacosDiscoveryProperties = SpringUtil.getBean(NacosDiscoveryProperties.class);
        int port = nacosDiscoveryProperties.getPort();
        if (port == -1) {
            port = SpringUtil.getBean(Environment.class).getProperty("server.port", Integer.class, 8080);
        }
        return nacosDiscoveryProperties.getIp() + ":" + port;
    }

    /**
     * 获取所有实例id
     *
     * @param serviceId
     * @return
     */
    public static List<String> getInstanceIds(String serviceId) {
        return getInstanceIds(null, serviceId);
    }

    /**
     * 获取所有实例id
     *
     * @param nacosDiscoveryClient
     * @param serviceId
     * @return
     */
    public static List<String> getInstanceIds(NacosDiscoveryClient nacosDiscoveryClient, String serviceId) {
        if (nacosDiscoveryClient == null) {
            nacosDiscoveryClient = SpringUtil.getBean(NacosDiscoveryClient.class);
        }

        List<String> instanceIds = nacosDiscoveryClient.getInstances(serviceId).stream().map(instance -> {
            String host = instance.getHost();
            int port = instance.getPort();
            return host + ":" + port;
        }).collect(Collectors.toList());
        return instanceIds;
    }

    /**
     * 获取所有实例id
     *
     * @return
     */
    public static List<String> getInstanceIds() {
        List<String> instanceIds = CollUtil.newArrayList();
        NacosDiscoveryClient nacosDiscoveryClient = SpringUtil.getBean(NacosDiscoveryClient.class);
        nacosDiscoveryClient.getServices().stream().forEach(serviceId -> {
            instanceIds.addAll(getInstanceIds(nacosDiscoveryClient, serviceId));
        });

        return instanceIds;
    }

    /**
     * 判断实例id是否存在
     *
     * @param instanceId
     * @return
     */

    public static boolean existInstanceId(String instanceId) {
        return getInstanceIds().contains(instanceId);
    }

    /**
     * 判断实例id是否存在
     *
     * @param serviceId
     * @param instanceId
     * @return
     */
    public static boolean existInstanceId(String serviceId, String instanceId) {
        return getInstanceIds(null, serviceId).contains(instanceId);
    }

}

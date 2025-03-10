package com.minimalism.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.List;

/**
 * @Author yan
 * @Date 2025/2/7 11:36:35
 * @Description
 */
public class NacosUtils {
    /**
     *
     * @param serviceId ==> 服务名
     * @param instanceId ==> ip:port
     * @param prefix ==> 前缀
     * @param path ==> 路径
     * @return
     */
    public static String getUrl(String serviceId, String instanceId, String prefix, String path) {
        String urlFormat = "http://%s:%s" + prefix + path;
        String url = null;
        DiscoveryClient discoveryClient = SpringUtil.getBean(DiscoveryClient.class);

        List<ServiceInstance> serviceInstances = discoveryClient.getInstances(serviceId);
        ServiceInstance serviceInstance = serviceInstances.stream()
                .filter(instance -> ObjectUtil.isNotEmpty(instanceId) || ObjectUtil.equals(instanceId, instance.getInstanceId()))
                .findFirst().orElse(null);
        if (serviceInstance != null) {
            String host = serviceInstance.getHost();
            int port = serviceInstance.getPort();
            url = String.format(urlFormat, host, port);
        }
        return url;
    }

    public static String getInstanceId() {
        NacosDiscoveryProperties nacosDiscoveryProperties = SpringUtil.getBean(NacosDiscoveryProperties.class);
        return nacosDiscoveryProperties.getIp() + ":" + nacosDiscoveryProperties.getPort();
    }

}

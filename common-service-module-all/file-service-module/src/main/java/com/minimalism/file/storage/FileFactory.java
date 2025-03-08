package com.minimalism.file.storage;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.bean.AbstractBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * @Author yan
 * @Date 2025/3/7 20:37:33
 * @Description
 */
public class FileFactory implements BeanFactoryAware, InitializingBean, AbstractBean {
    private ConfigurableListableBeanFactory beanFactory;
    private static List<IFileStorageClient> storages = CollUtil.newArrayList();
    private static int index = 0;
    public static IFileStorageClient getClient(StorageType storageType) {

        if (index == 0){
            Map<String, IFileStorageClient> beansOfType = SpringUtil.getBeansOfType(IFileStorageClient.class);
            beansOfType.forEach((key, value) -> {
                storages.add(value);
            });
            index++;
        }

        for (IFileStorageClient storage : storages) {
            if (storage.support(storageType)) {
                return storage;
            }
        }
        throw new IllegalArgumentException();
    }

    @Override
    @PostConstruct
    public void init() {
        AbstractBean.super.init();
        if (index == 0){
            Map<String, IFileStorageClient> beansOfType = SpringUtil.getBeansOfType(IFileStorageClient.class);
            beansOfType.forEach((key, value) -> {
                storages.add(value);
            });
            index++;
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, IFileStorageClient> beansOfType = beanFactory.getBeansOfType(IFileStorageClient.class);
        beansOfType.keySet().forEach(key -> {
            if (beanFactory.containsBeanDefinition(key)) {
                if (beanFactory.getBeanDefinition(key).isPrimary()) {
                    storages.add(beansOfType.get(key));
                }
            } else if (beanFactory.getParentBeanFactory() instanceof ConfigurableListableBeanFactory) {
                if (((ConfigurableListableBeanFactory) beanFactory.getParentBeanFactory()).containsBeanDefinition(key)) {
                    if (((ConfigurableListableBeanFactory) beanFactory.getParentBeanFactory()).getBeanDefinition(key).isPrimary()) {
                        storages.add(beansOfType.get(key));
                    }
                }
            }
        });
    }
}

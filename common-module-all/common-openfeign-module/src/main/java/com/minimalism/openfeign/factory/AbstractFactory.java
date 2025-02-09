package com.minimalism.openfeign.factory;

import cn.hutool.core.collection.CollUtil;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.openfeign.factory.interfaces.AbstractClient;
import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.List;
import java.util.Map;

/**
 * @author yan
 */
public class AbstractFactory implements BeanFactoryAware, InitializingBean, AbstractBean {
    private ConfigurableListableBeanFactory beanFactory;
    private static List<AbstractClient> abstractClients = CollUtil.newArrayList();

    public static AbstractClient getClient(AbstractEnum abstractEnum) {
        for (AbstractClient abstractClient : abstractClients) {
            if (abstractClient.support(abstractEnum)) {
                return abstractClient;
            }
        }
        throw new IllegalArgumentException();
    }

    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        info("abstractClients init ...");
        Map<String, AbstractClient> beansOfType = beanFactory.getBeansOfType(AbstractClient.class);
        beansOfType.keySet().forEach(key -> {
            if (beanFactory.containsBeanDefinition(key)) {
                if (beanFactory.getBeanDefinition(key).isPrimary()) {
                    abstractClients.add(beansOfType.get(key));
                }
            } else if (beanFactory.getParentBeanFactory() instanceof ConfigurableListableBeanFactory) {
                if (((ConfigurableListableBeanFactory) beanFactory.getParentBeanFactory()).containsBeanDefinition(key)) {
                    if (((ConfigurableListableBeanFactory) beanFactory.getParentBeanFactory()).getBeanDefinition(key).isPrimary()) {
                        abstractClients.add(beansOfType.get(key));
                    }
                }
            }
        });
    }
}

package com.minimalism.common.listener;

import com.minimalism.abstractinterface.bean.AbstractBean;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @Author yan
 * @Date 2025/3/21 10:06:21
 * @Description
 */
@Component
public class StartedEventListener implements ApplicationListener<ApplicationStartedEvent>, AbstractBean {
    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        info("onApplicationEvent:[{}]","应用启动完成，通知监听器执行缓存预加载操作");
    }
}

package com.minimalism.abstractinterface.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @Author yan
 * @Date 2024/9/22 上午10:46:15
 * @Description
 */
public interface AbstractBean {
    @Data @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    class LogBean{
       private org.slf4j.Logger logger;
       private Class<?> aClass;
    }
    @JsonIgnore
    default LogBean getLogBean(){
        LogBean logBean = new LogBean()
                .setAClass(this.getClass())
                .setLogger(LoggerFactory.getLogger(this.getClass()));
        return logBean;
    }
    @JsonIgnore
    default Logger getLogger(){
        return getLogBean().getLogger();
    }
    @JsonIgnore
    default Class<?> getAClass(){
        return getLogBean().getAClass();
    }
    /**
     * 初始化
     */
    @PostConstruct
    default void init(){
        LogBean logBean = getLogBean();
        logBean.getLogger().info("init {} ...",logBean.getAClass().getSimpleName());
    }

    /**
     * 销毁
     */
    @PreDestroy
    default void destroy() {
        LogBean logBean = getLogBean();
        logBean.getLogger().info("destroy {} ...",logBean.getAClass().getSimpleName());
    }
}

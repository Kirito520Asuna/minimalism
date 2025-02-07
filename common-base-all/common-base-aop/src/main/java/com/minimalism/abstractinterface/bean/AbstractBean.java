package com.minimalism.abstractinterface.bean;

import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.minimalism.utils.object.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @Author yan
 * @Date 2024/9/22 上午10:46:15
 * @Description
 */
public interface AbstractBean {
    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    class LogBean {
        private org.slf4j.Logger logger;
        private Class<?> aClass;
    }

    @JsonIgnore
    default LogBean getLogBean() {
        LogBean logBean = new LogBean()
                .setAClass(this.getClass())
                .setLogger(LoggerFactory.getLogger(this.getClass()));
        return logBean;
    }

    @JsonIgnore
    default Logger getLogger() {
        return getLogBean().getLogger();
    }

    @JsonIgnore
    default Class<?> getAClass() {
        return getLogBean().getAClass();
    }

    /**
     * 初始化
     */
    @PostConstruct
    default void init() {
        LogBean logBean = getLogBean();
        if (!isProd()) {
            logBean.getLogger().info("init {} ...", logBean.getAClass().getSimpleName());
        }
    }

    /**
     * 销毁
     */
    @PreDestroy
    default void destroy() {
        LogBean logBean = getLogBean();
        if (!isProd()) {
            logBean.getLogger().info("destroy {} ...", logBean.getAClass().getSimpleName());
        }
    }

    default boolean isProd() {
        Environment env = SpringUtil.getBean(Environment.class);
        String active = env.getProperty("spring.profiles.active");
        return ObjectUtils.equals("prod", active);
    }


    default boolean isTraceEnabled() {
        return getLogger().isTraceEnabled();
    }

    default void trace(String var1) {
        if (!isProd()) {
            getLogger().trace(var1);
        }
    }

    default void trace(String var1, Object var2) {
        if (!isProd()) {
            getLogger().trace(var1, var2);
        }
    }

    default void trace(String var1, Object var2, Object var3) {
        if (!isProd()) {
            getLogger().trace(var1, var2, var3);
        }
    }

    default void trace(String var1, Object... var2) {
        if (!isProd()) {
            getLogger().trace(var1, var2);
        }
    }

    default void trace(String var1, Throwable var2) {
        if (!isProd()) {
            getLogger().trace(var1, var2);
        }
    }

    default boolean isTraceEnabled(Marker var1) {
        return getLogger().isTraceEnabled(var1);
    }

    default void trace(Marker var1, String var2) {
        if (!isProd()) {
            getLogger().trace(var1, var2);
        }
    }

    default void trace(Marker var1, String var2, Object var3) {
        if (!isProd()) {
            getLogger().trace(var1, var2, var3);
        }
    }

    default void trace(Marker var1, String var2, Object var3, Object var4) {
        if (!isProd()) {
            getLogger().trace(var1, var2, var3, var4);
        }
    }

    default void trace(Marker var1, String var2, Object... var3) {
        if (!isProd()) {
            getLogger().trace(var1, var2);
        }
    }

    default void trace(Marker var1, String var2, Throwable var3) {
        if (!isProd()) {
            getLogger().trace(var1, var2);
        }
    }

    default boolean isDebugEnabled() {
        return getLogger().isDebugEnabled();
    }

    default void debug(String var1) {
        if (!isProd()) {
            getLogger().debug(var1);
        }
    }

    default void debug(String var1, Object var2) {
        if (!isProd()) {
            getLogger().debug(var1, var2);
        }
    }

    default void debug(String var1, Object var2, Object var3) {
        if (!isProd()) {
            getLogger().debug(var1, var2, var3);
        }
    }

    default void debug(String var1, Object... var2) {
        if (!isProd()) {
            getLogger().debug(var1, var2);
        }
    }

    default void debug(String var1, Throwable var2) {
        if (!isProd()) {
            getLogger().debug(var1, var2);
        }
    }

    default boolean isDebugEnabled(Marker var1) {
        return getLogger().isDebugEnabled(var1);
    }

    default void debug(Marker var1, String var2) {
        if (!isProd()) {
            getLogger().debug(var1, var2);
        }
    }

    default void debug(Marker var1, String var2, Object var3) {
        if (!isProd()) {
            getLogger().debug(var1, var2);
        }
    }

    default void debug(Marker var1, String var2, Object var3, Object var4) {
        if (!isProd()) {
            getLogger().debug(var1, var2);
        }
    }

    default void debug(Marker var1, String var2, Object... var3) {
        if (!isProd()) {
            getLogger().debug(var1, var2);
        }
    }

    default void debug(Marker var1, String var2, Throwable var3) {
        getLogger().debug(var1, var2);
    }

    default boolean isInfoEnabled() {
        return getLogger().isInfoEnabled();
    }

    default void info(String var1) {
        if (!isProd()) {
            getLogger().info(var1);
        }
    }

    default void info(String var1, Object var2) {
        if (!isProd()) {
            getLogger().info(var1, var2);
        }
    }

    default void info(String var1, Object var2, Object var3) {
        if (!isProd()) {
            getLogger().info(var1, var2, var3);
        }
    }

    default void info(String var1, Object... var2) {
        if (!isProd()) {
            getLogger().info(var1, var2);
        }
    }


    default void info(String var1, Throwable var2) {
        if (!isProd()) {
            getLogger().info(var1, var2);
        }
    }

    default boolean isInfoEnabled(Marker var1) {
        return getLogger().isInfoEnabled(var1);
    }

    default void info(Marker var1, String var2) {
        if (!isProd()) {
            getLogger().info(var1, var2);
        }
    }

    default void info(Marker var1, String var2, Object var3) {
        if (!isProd()) {
            getLogger().info(var1, var2, var3);
        }
    }

    default void info(Marker var1, String var2, Object var3, Object var4) {
        if (!isProd()) {
            getLogger().info(var1, var2, var3, var4);
        }
    }

    default void info(Marker var1, String var2, Object... var3) {
        if (!isProd()) {
            getLogger().info(var1, var2, var3);
        }
    }

    default void info(Marker var1, String var2, Throwable var3) {
        if (!isProd()) {
            getLogger().info(var1, var2, var3);
        }
    }

    default boolean isWarnEnabled() {
        return getLogger().isWarnEnabled();
    }

    default void warn(String var1) {
        if (!isProd()) {
            getLogger().warn(var1);
        }
    }

    default void warn(String var1, Object var2) {
        if (!isProd()) {
            getLogger().warn(var1, var2);
        }
    }

    default void warn(String var1, Object... var2) {
        if (!isProd()) {
            getLogger().warn(var1, var2);
        }
    }

    default void warn(String var1, Object var2, Object var3) {
        if (!isProd()) {
            getLogger().warn(var1, var2, var3);
        }
    }

    default void warn(String var1, Throwable var2) {
        if (!isProd()) {
            getLogger().warn(var1, var2);
        }
    }

    default boolean isWarnEnabled(Marker var1) {
        return getLogger().isWarnEnabled(var1);
    }

    default void warn(Marker var1, String var2) {
        if (!isProd()) {
            getLogger().warn(var1, var2);
        }
    }

    default void warn(Marker var1, String var2, Object var3) {
        if (!isProd()) {
            getLogger().warn(var1, var2, var3);
        }
    }

    default void warn(Marker var1, String var2, Object var3, Object var4) {
        if (!isProd()) {
            getLogger().warn(var1, var2, var3, var4);
        }
    }

    default void warn(Marker var1, String var2, Object... var3) {
        if (!isProd()) {
            getLogger().warn(var1, var2, var3);
        }
    }

    default void warn(Marker var1, String var2, Throwable var3) {
        if (!isProd()) {
            getLogger().warn(var1, var2, var3);
        }
    }

    default boolean isErrorEnabled() {
        return getLogger().isErrorEnabled();
    }

    default void error(String var1) {
        getLogger().error(var1);
    }

    default void error(String var1, Object var2) {
        getLogger().error(var1, var2);
    }

    default void error(String var1, Object var2, Object var3) {
        getLogger().error(var1, var2, var3);
    }

    default void error(String var1, Object... var2) {
        getLogger().error(var1, var2);
    }

    default void error(String var1, Throwable var2) {
        getLogger().error(var1, var2);
    }

    default boolean isErrorEnabled(Marker var1) {
        return getLogger().isErrorEnabled(var1);
    }

    default void error(Marker var1, String var2) {
        getLogger().error(var1, var2);
    }

    default void error(Marker var1, String var2, Object var3) {
        getLogger().error(var1, var2, var3);
    }

    default void error(Marker var1, String var2, Object var3, Object var4) {
        getLogger().error(var1, var2, var3, var4);
    }

    default void error(Marker var1, String var2, Object... var3) {
        getLogger().error(var1, var2, var3);
    }

    default void error(Marker var1, String var2, Throwable var3) {
        getLogger().error(var1, var2, var3);
    }
}

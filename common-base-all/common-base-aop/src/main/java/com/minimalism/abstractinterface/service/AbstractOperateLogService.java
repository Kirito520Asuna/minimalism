package com.minimalism.abstractinterface.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.aop.async.AsyncFuture;
import com.minimalism.enums.RequestMethod;
import com.minimalism.pojo.OperateLogInfo;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 操作日志  抽象Service 接口
 *
 * @author yan
 */
public interface AbstractOperateLogService extends AbstractBean {
    //Logger log = Logger.getLogger(AbstractOperateLogService.class.getName());
    JSONConfig jsonConfig = JSONConfig.create().setIgnoreNullValue(false);

    /**
     * 需要获取请求行的请求方法
     */
    List<String> getParamsMethod = Stream.
            of(RequestMethod.GET.name(), RequestMethod.DELETE.name()).collect(Collectors.toList());
    /**
     * 需要获取请求体的请求方法
     */
    List<String> getBodyMethod = Stream.
            of(RequestMethod.POST.name(), RequestMethod.PUT.name()).collect(Collectors.toList());

    //=====================同步=====================
    default void createOperateLog(Map<String, Object> operateLogMap) {
        if (ObjectUtil.isEmpty(operateLogMap)) {
            return;
        }
        OperateLogInfo operateLog = new OperateLogInfo();
        BeanUtil.copyProperties(operateLogMap, operateLog);
        createOperateLog(operateLog);
    }

    default void updateOperateLog(Map<String, Object> operateLogMap) {
        if (ObjectUtil.isEmpty(operateLogMap)) {
            return;
        }
        OperateLogInfo operateLog = new OperateLogInfo();
        BeanUtil.copyProperties(operateLogMap, operateLog);
        updateOperateLog(operateLog);
    }

    /**
     * 记录操作日志
     *
     * @param operateLog 操作日志请求
     */
    default void createOperateLog(OperateLogInfo operateLog) {
        org.slf4j.Logger log = getLogger();
        JSON json = JSONUtil.parse(operateLog, jsonConfig);
        log.debug("operateLog==>{}", json);
    }


    /**
     * 更新操作日志
     */
    default void updateOperateLog(OperateLogInfo operateLog) {
        org.slf4j.Logger log = getLogger();
        JSON json = JSONUtil.parse(operateLog, jsonConfig);
        log.info("operateLog==>{}", json);
    }


    //=====================异步=====================

    /**
     * 注意同实现类调同实现类的异步是不生效的
     * 因此需要将所有异步集中在异步server接口实现生效
     * 记录操作日志
     *
     * @param operateLog 操作日志请求
     */
    @AsyncFuture(useExecutor = true)
    default void createOperateLogAsync(OperateLogInfo operateLog) {
        org.slf4j.Logger log = getLogger();
        JSON json = JSONUtil.parse(operateLog, jsonConfig);
        log.debug("operateLog==>{}", json);
    }

    /**
     * 更新操作日志
     */
    @AsyncFuture(useExecutor = true)
    default void updateOperateLogAsync(OperateLogInfo operateLog) {
        org.slf4j.Logger log = getLogger();
        JSON json = JSONUtil.parse(operateLog, jsonConfig);
        log.debug("operateLog==>{}", json);
    }

}

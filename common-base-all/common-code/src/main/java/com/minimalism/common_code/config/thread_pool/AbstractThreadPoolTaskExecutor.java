package com.minimalism.common_code.config.thread_pool;

import com.minimalism.common_code.utils.thread.ThreadMdcUtil;
import org.slf4j.MDC;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @Author yan
 * @Date 2024/5/14 0014 15:00
 * @Description
 */
public abstract class AbstractThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {
    @Override
    public boolean prefersShortLivedTasks() {
        return super.prefersShortLivedTasks();
    }
    @Override
    public void execute(Runnable task) {
        super.execute(ThreadMdcUtil.wrap(task, MDC.getCopyOfContextMap()));
    }
    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(ThreadMdcUtil.wrap(task, MDC.getCopyOfContextMap()));
    }
    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(ThreadMdcUtil.wrap(task, MDC.getCopyOfContextMap()));
    }
}

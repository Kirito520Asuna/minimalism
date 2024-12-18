package com.minimalism.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @Author yan
 * @Date 2024/5/14 0014 16:59
 * @Description
 */
//@Configuration
//@EnableAsync
@Slf4j
public class ThreadPoolConfig {
    /**
     * 声明一个线程池
     *
     * @return 执行器
     */
    //@Bean("Executor")
    public Executor asyncExecutor() {
        log.info("-----------------asyncExecutor-----------------");
        ThreadPoolTaskExecutor executor = getThreadPoolTaskExecutor();
        //核心线程数5：线程池创建时候初始化的线程数
        executor.setCorePoolSize(5);
        //最大线程数5：线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
        executor.setMaxPoolSize(5);
        //缓冲队列500：用来缓冲执行任务的队列
        executor.setQueueCapacity(500);
        //允许线程的空闲时间60秒：当超过了核心线程出之外的线程在空闲时间到达之后会被销毁
        executor.setKeepAliveSeconds(60);
        //线程池名的前缀：设置好了之后可以方便我们定位处理任务所在的线程池
        executor.setThreadNamePrefix("asyncExecutor");
        executor.initialize();
        log.info("-----------------asyncExecutor-----------------");
        return executor;
    }

    public ThreadPoolTaskExecutor getThreadPoolTaskExecutor() {
        return new AbstractThreadPoolTaskExecutor();
    }
}
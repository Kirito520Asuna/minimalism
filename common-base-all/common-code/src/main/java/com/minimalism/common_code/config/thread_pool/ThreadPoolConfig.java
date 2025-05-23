package com.minimalism.common_code.config.thread_pool;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.Executor;

/**
 * @Author yan
 * @Date 2024/5/14 0014 16:59
 * @Description
 */
@Configuration
//@EnableAsync
@Slf4j
public class ThreadPoolConfig {
    private static final String GLOBAL_THREAD_POOL_TASK_EXECUTOR = "GLOBAL_THREAD_POOL_TASK_EXECUTOR";


    private static void MDCTaskDecorator(ThreadPoolTaskExecutor executor) {
        // 设置任务装饰器，以传递 MDC
        executor.setTaskDecorator(r -> {
            // 包装任务以传递 MDC
            Map<String, String> mdcContext = MDC.getCopyOfContextMap();
            return () -> {
                try {
                    if (mdcContext != null) {
                        // 设置 MDC 上下文
                        MDC.setContextMap(mdcContext);
                    }
                    // 执行任务
                    r.run();
                } finally {
                    // 清除 MDC 上下文
                    MDC.clear();
                }
            };
        });
    }

    // 配置默认异步 TaskExecutor
    @Bean(name = {"taskExecutor", "asyncExecutor", GLOBAL_THREAD_POOL_TASK_EXECUTOR}) // 注册为 Spring 默认异步执行器
    public ThreadPoolTaskExecutor globalThreadPoolTaskExecutor() {
        GlobalThreadPoolTaskExecutor executor = new GlobalThreadPoolTaskExecutor();
        //核心线程数5：线程池创建时候初始化的线程数
        executor.setCorePoolSize(5);
        //最大线程数5：线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
        executor.setMaxPoolSize(5);
        //缓冲队列500：用来缓冲执行任务的队列
        executor.setQueueCapacity(500);
        //允许线程的空闲时间60秒：当超过了核心线程出之外的线程在空闲时间到达之后会被销毁
        executor.setKeepAliveSeconds(60);
        //线程池名的前缀：设置好了之后可以方便我们定位处理任务所在的线程池
        executor.setThreadNamePrefix("Global-Task-"); // 线程名前缀
        MDCTaskDecorator(executor);
        executor.initialize();
        return executor;
    }

    /**
     * 声明一个线程池
     *
     * @return 执行器
     */
    //@Bean("Executor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = SpringUtil.getBean(GLOBAL_THREAD_POOL_TASK_EXECUTOR, ThreadPoolTaskExecutor.class);
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
        MDCTaskDecorator(executor);
        executor.initialize();
        return executor;
    }

}
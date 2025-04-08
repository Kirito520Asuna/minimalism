package com.minimalism.task.quartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 通用 Quartz 抽象任务类，包含任务执行时间记录和异常捕获
 */
public abstract class AbstractQuartzJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(AbstractQuartzJob.class);

    /**
     * 用于记录开始执行时间
     */
    private static final ThreadLocal<Date> startTimeThreadLocal = new ThreadLocal<>();

    @Override
    public void execute(JobExecutionContext context) {
        Object taskProperties = context.getMergedJobDataMap().get("TASK_PROPERTIES");
        try {
            before(context);
            doExecute(context);
            after(context, null);
        } catch (Exception e) {
            logger.error("任务执行异常：", e);
            after(context, e);
        }
    }

    /**
     * 执行前记录开始时间
     */
    protected void before(JobExecutionContext context) {
        startTimeThreadLocal.set(new Date());
    }

    /**
     * 执行后记录日志
     */
    protected void after(JobExecutionContext context, Exception e) {
        Date startTime = startTimeThreadLocal.get();
        startTimeThreadLocal.remove();

        Date endTime = new Date();
        long duration = endTime.getTime() - startTime.getTime();

        String jobName = context.getJobDetail().getKey().toString();

        if (e != null) {
            logger.error("任务 [{}] 执行失败，耗时 {} 毫秒，异常信息：{}", jobName, duration, e.getMessage());
        } else {
            logger.info("任务 [{}] 执行成功，耗时 {} 毫秒", jobName, duration);
        }

        // 如需写入日志数据库，可在这里扩展调用服务
    }

    /**
     * 子类实现具体任务逻辑
     */
    protected abstract void doExecute(JobExecutionContext context) throws Exception;
}

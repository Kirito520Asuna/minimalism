package com.minimalism.task.quartz.dstributed;

import cn.hutool.core.date.DatePattern;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author yan
 * @DateTime 2024/6/23 14:56:39
 * @Description 分布式 Job
 */
// 持久化
@PersistJobDataAfterExecution
// 禁止并发执行
@DisallowConcurrentExecution
public class DistributedJob extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        Logger log = LoggerFactory.getLogger(this.getClass());
        String taskName = context.getJobDetail().getJobDataMap().getString("name");
        log.info("===> Quartz job, time:{"+ DateTimeFormatter.ofPattern(DatePattern
                .NORM_DATETIME_PATTERN).format(LocalDateTime.now()) +"} ,name:{"+taskName+"} <===");
    }
}

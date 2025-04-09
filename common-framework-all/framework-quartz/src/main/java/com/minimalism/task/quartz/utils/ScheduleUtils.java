package com.minimalism.task.quartz.utils;

import org.quartz.JobKey;
import org.quartz.TriggerKey;

/**
 * @Author yan
 * @Date 2025/4/9 18:29:14
 * @Description
 */
public class ScheduleUtils {

    /**
     * 构建任务触发对象
     */
    public static TriggerKey buildTriggerKey(String name, String jobGroup) {
        return TriggerKey.triggerKey(name, jobGroup);
    }

    /**
     * 构建任务键对象
     */
    public static JobKey buildJobKey(String name, String jobGroup) {
        return JobKey.jobKey(name, jobGroup);
    }

}

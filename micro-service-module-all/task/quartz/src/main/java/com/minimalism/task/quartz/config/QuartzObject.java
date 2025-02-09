package com.minimalism.task.quartz.config;

import com.minimalism.task.quartz.enums.CronTemplate;
import com.minimalism.task.quartz.enums.QuartzGroup;
import com.minimalism.task.quartz.enums.QuartzName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.quartz.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class QuartzObject {
    private Class<Job> clockJobClass;
    private QuartzGroup quartzGroup;
    private QuartzName quartzName;
    private CronTemplate cronTemplate;
    private String value;
    private JobKey jobKey;
    private TriggerKey triggerKey;
    private CronScheduleBuilder scheduleBuilder;
    private JobDetail jobDetail;
    private Trigger trigger;
}

package com.minimalism.pojo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.minimalism.enums.JobType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2024/6/20 0020 17:42:21
 * @Description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Builder
public class TaskJob {
    @Schema(description = "任务名称")
    private String jobName;
    @Schema(description = "任务分组")
    private String jobGroup;
    @Schema(description = "任务类型")
    private JobType jobType;
    @Schema(description = "任务表达式模板")
    private String cronTemplate;
    @Schema(description = "(,分割)任务表达式值")
    private String value;
    @Schema(description = "(正式有效值)任务表达式")
    private String cronExpression;

    public String getCronTemplate() {
        JobType jobType = getJobType();
        if (ObjectUtil.isNotEmpty(jobType) && StrUtil.isNotBlank(jobType.getCronTemplate())) {
            return jobType.getCronTemplate();
        }
        return cronTemplate;
    }

    public String getCronExpression() {
        String cronTemplate = null;
        JobType jobType = getJobType();
        if (ObjectUtil.isNotEmpty(jobType) && StrUtil.isNotBlank(jobType.getCronTemplate())) {
            cronTemplate = jobType.getCronTemplate();
        }
        String valueStr = getValue();
        if (StrUtil.isNotBlank(cronTemplate) && StrUtil.isNotBlank(valueStr)) {
            List<String> stringList = Arrays.stream(valueStr.replace(" ", "").replace("，", ",")
                    .split(",")).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(stringList)) {
                // 替换模板中的%s
                return String.format(cronTemplate, stringList.toArray());
            }
        }
        return cronExpression;
    }

    public static void main(String[] args) {
        //String s = "defr%sfrf%sfrki%s";
        //List<String> list = CollUtil.newArrayList("1真1", "2真2", "3真3");
        //System.out.println(String.format(s, list.toArray()));

        TaskJob taskJob = TaskJob.builder().jobType(JobType.CLOCK).build().setValue("1");
        System.out.println(taskJob);
    }
}

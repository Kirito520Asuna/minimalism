package com.minimalism.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JobType {
    CLOCK("点钟任务", "0 0 %s * * ?"),
    HOUR("小时任务", "0 0 0/%s * * ?"),
    MINUTE("分钟任务", "0 0/%s * * * ?"),
    SYSTEM("系统任务", null),
    CUSTOM("自定义任务", null);
    String desc;
    String cronTemplate;
}

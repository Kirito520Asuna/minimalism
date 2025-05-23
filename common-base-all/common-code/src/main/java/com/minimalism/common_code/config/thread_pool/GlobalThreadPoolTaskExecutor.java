package com.minimalism.common_code.config.thread_pool;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * @Author yan
 * @Date 2025/4/23 00:10:09
 * @Description
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class GlobalThreadPoolTaskExecutor extends AbstractThreadPoolTaskExecutor {
}

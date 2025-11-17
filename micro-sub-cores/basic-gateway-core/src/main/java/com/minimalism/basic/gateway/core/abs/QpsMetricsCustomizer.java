package com.minimalism.basic.gateway.core.abs;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;

/**
 * @Author yan
 * @Date 2025/11/17 23:14:59
 * @Description
 */
public interface QpsMetricsCustomizer {
    //增强全局 QPS 指标：按路由、按状态码、按异常类型
    default MeterRegistryCustomizer<MeterRegistry> MetricsCustomizer() {
        return registry -> {
            Tags commonTags = Tags.of("application", "gateway");
            //全局请求计数器（可替代默认 http.server.requests）
            Counter.builder("gateway.requests.total")
                    .description("gateway request total")
                    .register(registry);
            //更细粒度的 Timer（包含路由信息）
            Timer.builder("gateway.requests")
                    .tags(commonTags)
                    .description("gateway request timer")
                    .publishPercentileHistogram(true)
                    .publishPercentiles(0.5, 0.90, 0.95, 0.99)
                    .register(registry);
        };
    }
}

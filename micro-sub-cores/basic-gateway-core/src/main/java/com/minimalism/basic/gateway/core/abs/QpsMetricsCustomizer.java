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
    //
    default MeterRegistryCustomizer<MeterRegistry> MetricsCustomizer() {
        return registry -> {
            Tags commonTags = Tags.of("application", "gateway");
            //
            Counter.builder("gateway.requests.total")
                    .description("gateway request total")
                    .register(registry);
            //
            Timer.builder("gateway.requests")
                    .tags(commonTags)
                    .description("gateway request timer")
                    .publishPercentileHistogram(true)
                    .publishPercentiles(0.5, 0.90, 0.95, 0.99)
                    .register(registry);
        };
    }
}

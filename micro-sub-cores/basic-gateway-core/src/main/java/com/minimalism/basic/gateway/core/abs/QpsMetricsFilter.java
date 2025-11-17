package com.minimalism.basic.gateway.core.abs;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

/**
 * @Author yan
 * @Date 2025/11/17 23:33:32
 * @Description
 */
public interface QpsMetricsFilter extends GlobalFilter, Ordered {
    default String startName(){
        return "startTime";
    }
    @Override
    default Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        exchange.getAttributes().put(startName(),System.currentTimeMillis());

        return chain.filter(exchange).then(
                Mono.fromRunnable(() -> {
                    Long start = exchange.getAttribute(startName());
                    if (start != null) {
                        long duration = System.currentTimeMillis() - start;

                        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
                        String routeId = route != null ? route.getId() : "unknown";
                        String path = exchange.getRequest().getPath().value();
                        int status = exchange.getResponse().getStatusCode() != null ?
                                exchange.getResponse().getStatusCode().value() : 999;

                        Tags tags = Tags.of(
                                "route", routeId,
                                "path", path,
                                "status", String.valueOf(status),
                                "method", exchange.getRequest().getMethod().name()
                        );

                        // 计数器（QPS 核心）
                        Counter.builder("gateway.requests.total")
                                .tags(tags)
                                .register(Metrics.globalRegistry)
                                .increment();

                        // 延迟分布
                        Timer.builder("gateway.requests.duration")
                                .tags(tags)
                                .description("Gateway request duration")
                                .publishPercentileHistogram(true)
                                .register(Metrics.globalRegistry)
                                .record(duration, TimeUnit.MILLISECONDS);
                    }
                })
        );
    }

    @Override
    default int getOrder() {
        return 0;
    }
}

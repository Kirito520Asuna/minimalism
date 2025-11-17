package com.minimalism.basic.gateway.core.abs;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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
        return null;
    }

    @Override
    default int getOrder() {
        return 0;
    }
}

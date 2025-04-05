package com.minimalism.gateway.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.google.common.collect.Maps;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.constant.gateway.GatewayConstants;
import com.minimalism.gateway.config.GatewayConfig;
import com.minimalism.gateway.filter.order.Order;
import com.minimalism.utils.object.ObjectUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

public class GatewayDomainsHeaderFilter implements GlobalFilter, Ordered, AbstractBean {


    @Override
    public int getOrder() {
        return Order.GATEWAY_DOMAINS_ORDER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        debug("[Order:{}]==>GatewayDomains<==",getOrder());
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest.Builder mutate = request.mutate();

        Environment env = SpringUtil.getBean(Environment.class);
        Map<String, String> map = fetchDomains(env);
        map.entrySet().stream().forEach(entry -> {
            String value = entry.getValue();
            String key = entry.getKey();
            if (ObjectUtils.equals(HttpHeaders.ACCEPT_ENCODING, key)) {
                value = "";
            }
            mutate.header(key, value);
        });

        ServerHttpRequest build = mutate.build();

        ServerWebExchange webExchange = exchange.mutate().request(build).build();
        return chain.filter(webExchange);
    }

    private static Map<String, String> fetchDomains(Environment env) {
        Map<String, String> map = Maps.newLinkedHashMap();

        String domains = env.getProperty("domains.name", "");
        String domainsAllPaths = env.getProperty("domains.all-paths", "");
        String prefix = GatewayConfig.getPrefix();

        if (StrUtil.isNotBlank(prefix)) {
            map.put(GatewayConstants.GATEWAY_PREFIX, prefix);
        }

        if (StrUtil.isNotBlank(prefix)) {
            map.put(GatewayConstants.GATEWAY_DOMAINS_NAME, domains);
        }

        if (StrUtil.isNotBlank(prefix)) {
            map.put(GatewayConstants.GATEWAY_DOMAINS_ALL_PATHS, domainsAllPaths);
        }
        return map;
    }
}

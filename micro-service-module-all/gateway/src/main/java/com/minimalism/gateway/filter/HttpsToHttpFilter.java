package com.minimalism.gateway.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.google.common.collect.Maps;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.constant.gateway.GatewayConstants;
import com.minimalism.gateway.config.GatewayConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author minimalism
 * @Date 2024/9/17 上午2:40:43
 * @Description
 */

@Slf4j
public class HttpsToHttpFilter implements GlobalFilter, Ordered, AbstractBean {
    private static final int HTTPS_TO_HTTP_FILTER_ORDER = 10099;
    @Override
    public int getOrder() {
        return HTTPS_TO_HTTP_FILTER_ORDER;
    }
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        info("HttpsToHttpFilter filter");
        URI originalUri = exchange.getRequest().getURI();
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest.Builder mutate = request.mutate();
        String forwardedUri = request.getURI().toString();

        Environment env = SpringUtil.getBean(Environment.class);
        Boolean httpsToHttp = env.getProperty("cors.gateway.https-to-http-filter", Boolean.class, true);

        if (httpsToHttp != null && httpsToHttp) {
            httpsToHttp(originalUri, mutate, forwardedUri);
        }

        Map<String, String> map = getDomainsHeader(env);

        map.entrySet().stream().forEach(entry -> {
            mutate.header(entry.getKey(), entry.getValue());
        });

        ServerHttpRequest build = mutate.build();
        ServerWebExchange webExchange = exchange.mutate().request(build).build();
        return chain.filter(webExchange);
    }

    private static Map<String, String> getDomainsHeader(Environment env) {
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

    private void httpsToHttp(URI originalUri, ServerHttpRequest.Builder mutate, String forwardedUri) {
        if (forwardedUri != null && forwardedUri.startsWith("https")) {
            info("Open HttpsToHttp");
            try {
                URI mutatedUri = new URI("http",
                        originalUri.getUserInfo(),
                        originalUri.getHost(),
                        originalUri.getPort(),
                        originalUri.getPath(),
                        originalUri.getQuery(),
                        originalUri.getFragment());
                mutate.uri(mutatedUri);
            } catch (Exception e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }

}
package com.minimalism.gateway.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.google.common.collect.Maps;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.constant.gateway.GatewayConstants;
import com.minimalism.gateway.config.GatewayConfig;
import com.minimalism.gateway.filter.order.Order;
import com.minimalism.utils.object.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
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

    @Override
    public int getOrder() {
        return Order.HTTPS_TO_HTTP_ORDER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        debug("HttpsToHttpFilter filter Order==>[{}]", getOrder());
        URI originalUri = exchange.getRequest().getURI();
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest.Builder mutate = request.mutate();
        String forwardedUri = request.getURI().toString();

        Environment env = SpringUtil.getBean(Environment.class);
        Boolean httpsToHttp = env.getProperty("cors.gateway.https-to-http-filter", Boolean.class, false);

        if (httpsToHttp != null && httpsToHttp) {
            httpsToHttp(originalUri, mutate, forwardedUri);
        }

        ServerHttpRequest build = mutate.build();

        ServerWebExchange webExchange = exchange.mutate().request(build).build();
        return chain.filter(webExchange);
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
package com.minimalism.gateway.filter;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.gateway.filter.order.Order;
import com.minimalism.properties.CorsProperties;
import com.minimalism.utils.object.ObjectUtils;
import com.minimalism.utils.str.StrUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class CorsHeaderFilter implements GlobalFilter, Ordered, AbstractBean {
    @Override
    public int getOrder() {
        return Order.CORS_HEADER_ORDER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        debug("[Order:{}]==>CorsHeader<==", getOrder());
        HttpHeaders headers = exchange.getResponse().getHeaders();

        String allowedOrigin = null;
        String allowedHeader = null;
        Boolean allowCredentials = null;
        Long maxAge = null;
        String allowedMethods = null;

        try {
            CorsProperties cors = SpringUtil.getBean(CorsProperties.class);
            debug("CorsProperties:{}", JSONUtil.toJsonStr(cors, JSONConfig.create().setIgnoreNullValue(false)));
            allowedOrigin = cors.getAllowedOrigin();
            allowedHeader = cors.getAllowedHeader();
            allowCredentials = cors.getAllowCredentials();
            maxAge = cors.getMaxAge();
            allowedMethods = cors.getAllowedMethods();
        } catch (Exception e) {
            warn("CorsProperties is null");
        }
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ObjectUtils.defaultIfEmpty(allowedOrigin, "*"));
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, String.valueOf(ObjectUtils.defaultIfEmpty(allowCredentials, true)));

        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, StrUtils.trim(ObjectUtils.defaultIfEmpty(allowedMethods, "GET,POST,PUT,DELETE,OPTIONS,PATCH,HEAD")));
        headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, String.valueOf(ObjectUtils.defaultIfEmpty(maxAge, Long.valueOf(360000l))));
        //headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type, Authorization, X-Requested-With");
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, ObjectUtils.defaultIfEmpty(allowedHeader, "*"));

        // OPTIONS请求提前返回
        if (ObjectUtils.equals(exchange.getRequest().getMethod(), HttpMethod.OPTIONS)) {
            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.OK);
            debug("==>CorsHeader::{}<==", HttpMethod.OPTIONS);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

}

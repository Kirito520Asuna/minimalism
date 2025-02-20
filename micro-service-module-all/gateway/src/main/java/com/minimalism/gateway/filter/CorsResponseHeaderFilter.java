package com.minimalism.gateway.filter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.minimalism.abstractinterface.bean.AbstractBean;

import com.minimalism.utils.object.ObjectUtils;
import lombok.extern.slf4j.Slf4j;

import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.core.Ordered;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;


/**
 * @Author minimalism
 * @Date 2024/11/14 下午12:34:07
 * @Description
 */
@Slf4j
public class CorsResponseHeaderFilter implements GlobalFilter, Ordered, AbstractBean {

    @Override
    public int getOrder() {
        // 即待处理完响应体后接着处理响应头
        // 比 NettyWriteResponseFilter 先执行，确保响应头是可修改的
        return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 2;
    }


    public Mono getFromRunnableMono(ServerWebExchange exchange, GatewayFilterChain chain) {
        Mono<Object> fromRunnable = Mono.fromRunnable(() -> {
            try {
                ServerHttpResponse response = exchange.getResponse();
                HttpHeaders headers = response.getHeaders();
                info("处理前的header头: {}", JSONUtil.toJsonStr(headers));
                // 创建新Header Map（避免修改不可变Entry）
                Map<String, List<String>> newHeaders = new HashMap<>();
                response.getHeaders().add("Content-Type","application/json;charset=UTF-8");

                headers.forEach((key, values) -> {
                    // 去重处理
                    List<String> distinctValues = values.stream()
                            .distinct()
                            .collect(Collectors.toList());
                    // 保留第一个值（如果需特殊处理）
                    /*if (distinctValues.size() > 1) {
                        distinctValues = CollUtil.newArrayList(distinctValues.get(0));
                    }*/
                    newHeaders.put(key, distinctValues);
                });

                // 清空并重置Header
                headers.clear();
                headers.putAll(newHeaders);
                info("处理后的header头: {}", JSONUtil.toJsonStr(headers));
            } catch (Exception e) {
                error("去除重复请求头异常: ", e);
            }
        });

        return fromRunnable;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {

            ServerHttpResponse response = exchange.getResponse();
            ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(response) {
                @Override
                public HttpHeaders getHeaders() {
                    HttpHeaders headers = new HttpHeaders();
                    headers.putAll(super.getHeaders());
                    headers.put("content-type",CollUtil.newArrayList("application/json; charset=UTF-8"));
                    return headers;
                }

                @Override
                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {

                    String contentType = getDelegate().getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
                    Boolean flag = MediaType.APPLICATION_JSON_VALUE.equals(contentType) || MediaType.APPLICATION_JSON_UTF8_VALUE.equals(contentType);
                    if (body instanceof Flux && flag) {
                        Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                        return super.writeWith(fluxBody.buffer().map(dataBuffer -> {
                            // 合并 DataBuffer
                            DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                            DataBuffer join = dataBufferFactory.join(dataBuffer);
                            byte[] content = new byte[join.readableByteCount()];
                            join.read(content);

                            // 释放内存
                            DataBufferUtils.release(join);
                            DataBufferFactory bufferFactory = response.bufferFactory();

                            // 使用 UTF-8 直接转换为字节数组，不必再做字符串转码
                            return bufferFactory.wrap(content);
                        }));
                    }
                    return super.writeWith(body);
                }
            };

            ServerWebExchange mutatedExchange = exchange.mutate()
                    .response(decoratedResponse)
                    .build();
            Mono distinctResponseHeaders = getFromRunnableMono(mutatedExchange, chain);
            return chain.filter(mutatedExchange).then(distinctResponseHeaders);
        } catch (Exception e) {
            error("去除重复请求头error,{},{}", e);
        }


        return chain.filter(exchange);
    }


}
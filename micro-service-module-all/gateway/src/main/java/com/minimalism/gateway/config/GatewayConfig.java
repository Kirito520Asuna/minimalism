package com.minimalism.gateway.config;

import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.gateway.filter.*;
import com.minimalism.properties.CorsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;


/**
 * @Author minimalism
 * @Date 2023/10/26 0026 15:58
 * @Description
 */
@Slf4j
@Configuration
public class GatewayConfig implements AbstractBean {
    public static String getPrefix() {
        String prefix = SpringUtil.getBean(Environment.class).getProperty("server.servlet.context-path");
        prefix = StringUtils.isEmpty(prefix) ? "" : prefix;
        return prefix;
    }

    /**
     * 过滤 server.servlet.context-path 属性配置的项目路径，防止对后续路由策略产生影响，因为 gateway 网关不支持 servlet
     */
    @Bean
    @Order(-1)
    public WebFilter webFilter() {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getRawPath();
            String prefix = getPrefix();
            path = prefix != null && !"".equals(prefix)
                    && path.startsWith(prefix)
                    && !path.startsWith(prefix + "/home")
                    && !path.startsWith(prefix + "/api-path")
                    && !path.startsWith(prefix + "/doc.html")
                    && !path.startsWith(prefix + "/swagger-ui.html")
                    ? path.replaceFirst(prefix, "") : path;

            ServerHttpRequest newRequest = request.mutate().path(path).build();
            ServerWebExchange webExchange = exchange.mutate().request(newRequest).build();
            Mono<Void> filter = chain.filter(webExchange);
            return filter;
        };
    }

    /**
     * spring:
     * cloud:
     * gateway:
     * default-filters:
     * # 解决响应头重复问题  options
     * - DedupeResponseHeader=Vary Access-Control-Allow-Origin Access-Control-Allow-Credentials, RETAIN_FIRST
     *
     * @return
     */
    //@Bean
    @Deprecated
    @ConditionalOnExpression("${cors.gateway.distinct-headers-filter: true}")
    public CorsResponseHeaderFilter corsResponseHeaderFilter() {
        debug("corsResponseHeaderFilter init ...");
        return new CorsResponseHeaderFilter();
    }
    @Bean
    @ConditionalOnExpression("${cors.gateway.distinct-headers-filter: true}")
    public DistinctResponseHeaderFilter distinctResponseHeaderFilter() {
        return new DistinctResponseHeaderFilter();
    }

    @Bean
    public CorsHeaderFilter corsHeaderFilter(){
        return new CorsHeaderFilter();
    }

    @Bean
    public GatewayDomainsHeaderFilter gatewayDomainsHeaderFilter(){
        return new GatewayDomainsHeaderFilter();
    }

    @Bean
    @ConditionalOnExpression("${cors.gateway.https-to-http-filter: false}")
    public HttpsToHttpFilter httpsToHttpFilter() {
        return new HttpsToHttpFilter();
    }
}
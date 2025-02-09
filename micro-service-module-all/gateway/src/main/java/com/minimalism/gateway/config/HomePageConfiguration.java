package com.minimalism.gateway.config;

import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.bean.AbstractBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


/**
 * @author minimalism
 */
@Profile(value = {"dev", "test"})
@Configuration(proxyBeanMethods = false)
public class HomePageConfiguration implements WebFluxConfigurer , AbstractBean {
    public String getPath() {
        String prefix = GatewayConfig.getPrefix();
        return prefix;
    }

    @Bean
    public RouterFunction<ServerResponse> homeRoute(@Value("classpath:/static/index-${spring.profiles.active}.html") final Resource index) {
        return RouterFunctions.route(RequestPredicates.GET(getPath() + "/home"), request -> ServerResponse.ok().contentType(MediaType.TEXT_HTML).bodyValue(index));
    }

    @Bean
    public RouterFunction<ServerResponse> staticResourceLocator() {
        return RouterFunctions.resources(getPath() + "/assert/**", new ClassPathResource("/static/**"));
    }
}

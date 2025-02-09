package com.minimalism.gateway.web;

import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.result.Result;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static com.minimalism.result.Result.ok;


/**
 * @author Mysteriousman
 */
@RequiredArgsConstructor
@RestController
@Profile(value = {"dev", "test"})
@RequestMapping(value = "${server.servlet.context-path}")
public class HomeControllerWebMvc implements AbstractBean {
    private final RouteLocator routeLocator;

    @GetMapping(value = "/api-path")
    public Result<?> homePage() {
        Flux<Route> routes = routeLocator.getRoutes();
        List<RoutePath> routePaths = new ArrayList<>();
        routes.subscribe(route -> {
            RoutePath routePath = new RoutePath();
            String needParse = route.getPredicate().toString();
            String service = needParse.substring(needParse.indexOf("/"), needParse.lastIndexOf("/"));
            String path = route.getMetadata().get("document-uri") == null ? service.concat("/swagger-ui/index.html") : service.concat((String) route.getMetadata().get("document-uri"));
            Object summary = route.getMetadata().get("summary") == null ? "" : route.getMetadata().get("summary");
            Object description = route.getMetadata().get("description") == null ? "" : route.getMetadata().get("description");
            routePath.setPath(path);
            routePath.setSummary(summary.toString());
            routePath.setDescription(description.toString());
            routePaths.add(routePath);
        });
        return ok(routePaths.stream().filter(routePath -> !routePath.getSummary().isEmpty()));
    }

    @Data
    static class RoutePath {
        private String path;
        private String summary;
        private String description;
    }
}

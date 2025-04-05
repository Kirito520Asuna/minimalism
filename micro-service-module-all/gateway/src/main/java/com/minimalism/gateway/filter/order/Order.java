package com.minimalism.gateway.filter.order;

public interface Order {
    int CORS_HEADER_ORDER = -100;
    int GATEWAY_DOMAINS_ORDER = 100;
    int HTTPS_TO_HTTP_ORDER = 200;
    int DISTINCT_RESPONSE_ORDER = 300;
}

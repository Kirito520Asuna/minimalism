package com.minimalism.properties;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minimalism.abstractinterface.service.properties.BeanProperties;
import com.minimalism.constant.PropertiesConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @Author minimalism
 * @Date 2024/11/14 下午1:45:27
 * @Description
 */

@Accessors(chain = true)
@Configuration
@Data @RefreshScope
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = PropertiesConstants.CORS_GATEWAY_PREFIX)
public class CorsGatewayProperties implements BeanProperties {
    @JsonProperty(PropertiesConstants.CORS_GATEWAY_DEFAULT_FILTER)
    private Boolean defaultFilter = false;
    @JsonProperty(PropertiesConstants.CORS_GATEWAY_WEB_FILTER)
    private Boolean webFilter = true;
    @JsonProperty(PropertiesConstants.CORS_GATEWAY_DISTINCT_HEADERS_FILTER)
    private Boolean distinctHeadersFilter = true;
}

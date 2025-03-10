package com.minimalism.properties;

import com.minimalism.abstractinterface.service.properties.BeanProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * 验证码配置
 * 
 * @author yan
 */
@ConditionalOnExpression("${captcha.enabled:false}")
@Accessors(chain = true)
@Configuration @NoArgsConstructor
@RefreshScope @Data @AllArgsConstructor
@ConfigurationProperties(prefix = "captcha")
public class CaptchaProperties implements BeanProperties {
    /**
     * 验证码开关
     */
    private Boolean enabled = false;

    /**
     * 验证码类型（math 数组计算 char 字符）
     */
    private String type = "math";

}

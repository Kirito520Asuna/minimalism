package com.minimalism.openfeign.config.seata;

import com.minimalism.abstractinterface.bean.AbstractBean;
import io.seata.config.springcloud.EnableSeataSpringConfig;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yan
 * @Date 2024/5/15 0015 15:01
 * @Description
 */
@Configuration
@EnableSeataSpringConfig
public class SeataConfig implements AbstractBean {
}

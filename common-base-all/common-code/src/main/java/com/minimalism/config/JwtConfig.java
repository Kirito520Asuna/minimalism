package com.minimalism.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.bean.AbstractBean;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @Author yan
 * @Date 2024/10/14 上午12:40:00
 * @Description
 */
@Slf4j
@Configuration
@Data
@NoArgsConstructor
@AllArgsConstructor
//@ConstructorBinding
//@ConfigurationProperties(prefix = "common.token")
public class JwtConfig implements AbstractBean {
    /**
     * 默认token名称
     */
    @Value("${common.jwt.tokenName:token}")
    private String tokenName = "token";
    /**
     * 默认刷新Token名称
     */
    @Value("${common.jwt.refreshTokenName:refreshToken}")
    private String refreshTokenName = "refreshToken";
    /**
     * 是否开启双token
     */
    @Value("${common.jwt.openTwoToken:false}")
    private Boolean openTwoToken = false;
    /**
     * 单token通过后 再次验证过期时间时长 以便于刷新token(分钟为单位)
     */
    @Value("${common.jwt.refreshOneToken:5}")
    private Long refreshOneTokenLong = 5l;
    @Value("${common.jwt.path:/jwt/**}")
    private String jwtPath = "/jwt/**";
    @Value("${common.jwt.openFilter:true}")
    private Boolean openFilter = true;
    @Value("${common.jwt.openInterceptor:false}")
    private Boolean openInterceptor = false;

    @Override
    public void init() {
        AbstractBean.super.init();
        Environment env = SpringUtil.getBean(Environment.class);

        Boolean openInterceptor = env.getProperty("common.jwt.openInterceptor", Boolean.class);
        Boolean openFilter = env.getProperty("common.jwt.openFilter", Boolean.class);
        openInterceptor = ObjectUtil.defaultIfNull(openInterceptor, false)
                && !ObjectUtil.defaultIfNull(openFilter, true);
        this.openInterceptor = openInterceptor;
    }
}

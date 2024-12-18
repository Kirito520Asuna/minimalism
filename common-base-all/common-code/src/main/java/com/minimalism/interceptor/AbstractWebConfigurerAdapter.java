package com.minimalism.interceptor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.abstractinterface.AbstractInterceptor;
import com.minimalism.abstractinterface.bean.AbstractBean;
import com.minimalism.config.ApiConfig;
import com.minimalism.config.JwtConfig;
import com.minimalism.interceptor.Impl.DefaultApiInterceptor;
import com.minimalism.interceptor.Impl.DefaultInterceptor;
import com.minimalism.interceptor.Impl.DefaultLogInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2024/10/27 下午10:47:59
 * @Description
 */
public interface AbstractWebConfigurerAdapter extends AbstractBean {
    default void initInterceptors(InterceptorRegistry registry) {
        //getLogger().info("注册拦截器");
        //注册拦截器
        registry.addInterceptor(SpringUtil.getBean(DefaultInterceptor.class));

        String apiPath = ObjectUtil.
                defaultIfNull(SpringUtil.getBean(ApiConfig.class).getApiPath(), "/api/**");
        if (!apiPath.endsWith("**")) {
            apiPath = new StringBuffer(apiPath).append("**").toString();
        }

        registry.addInterceptor(SpringUtil.getBean(AbstractApiInterceptor.class))
                .addPathPatterns(apiPath);

        String jwtPath = ObjectUtil.
                defaultIfNull(SpringUtil.getBean(JwtConfig.class).getJwtPath(), "/jwt/**");

        List<String> jwtPaths = Arrays.stream(jwtPath.split(",")).collect(Collectors.toList());

        registry.addInterceptor(SpringUtil.getBean(AbstractLogInInterceptor.class))
                .addPathPatterns(jwtPaths);
        //List<String> excludeList = CollUtil.newArrayList(jwtPaths);
        //excludeList.add(apiPath);

        //可以具体制定哪些需要拦截，哪些不拦截，其实也可以使用自定义注解更灵活完成
//                .addPathPatterns("/**")
//                .excludePathPatterns("/testxx.html");
    }

    ;
}

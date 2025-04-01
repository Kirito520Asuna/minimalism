package com.minimalism;

import com.minimalism.common.service.impl.mp.CommonMpServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@ComponentScan(basePackages = "com.minimalism",excludeFilters = {
        //@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {/*CommonUserService.class,*/ CommonMpServiceImpl.class})
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.minimalism.common.service.impl.mp.*")
})
@SpringBootApplication
public class KeyPairApplication {

    public static void main(String[] args) {
        SpringApplication.run(KeyPairApplication.class, args);
    }

}

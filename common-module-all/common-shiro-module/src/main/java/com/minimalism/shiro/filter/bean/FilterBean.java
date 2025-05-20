//package com.yan.filter.bean;
//
//import com.yan.filter.JwtAuthFilter;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Component;
//
///**
// * @Author yan
// * @Date 2024/10/28 下午1:56:09
// * @Description
// */
//@Component
//@ConditionalOnExpression("${common.jwt.openFilter:true}&&!${common.jwt.openInterceptor:false}")
//public class FilterBean {
//    //@Bean
//    public JwtAuthFilter jwtAuthFilter() {
//        return new JwtAuthFilter();
//    }
//}

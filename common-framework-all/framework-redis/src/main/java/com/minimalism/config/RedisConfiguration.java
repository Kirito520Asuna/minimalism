package com.minimalism.config;

import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Configuration;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2024/5/23 0023 10:41
 * @Description
 */
@Configuration
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true) @Slf4j
public class RedisConfiguration {
    @Value("${spring.redis.host:127.0.0.1}")
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private int redisPort;

    @Value("${spring.redis.password:#{null}}")
    private String redisPassword;

    @Value("${spring.redis.timeout:#{null}}")
    private Integer redisTimeout;

    @Value("${spring.redis.mode:single}")
    private String redisMode; // 可配置为 "single" 或 "cluster"
    @Value("${spring.redis.cluster.nodes:redis://127.0.0.1:6379}")
    private String clusterNodeStr;
    @Value("${spring.redis.cluster.nodes:redis://127.0.0.1:6379}")
    private List<String> clusterNodes = CollUtil.newArrayList();
    @Resource
    private RedisProperties redisProperties;
  public enum RedisMode {
        single,sentinel, cluster;
    }
    public RedisMode getRedisModeEnum(){
        RedisMode mode = RedisMode.single;
        try {
            mode = RedisMode.valueOf(redisMode);
        } catch (IllegalArgumentException e) {
            log.error("redis mode is illegal");
        }
        return mode;
    }
    public List<String> getAddresses() {
        String template = "redis://%s:%s";
        String format = String.format(template, redisHost, redisPort);

        List<String> addresses = new ArrayList<>();
        RedisMode mode = RedisMode.single;
        try {
            mode = RedisMode.valueOf(redisMode);
        } catch (IllegalArgumentException e) {
            log.error("redis mode is illegal");
        }

        switch (mode) {
            case cluster:
            case sentinel:
                List<String> collect = Arrays.stream(clusterNodeStr.replace(" ", "")
                        .replace("，", ",")
                        .split(",")).distinct().collect(Collectors.toList());
                addresses.addAll(collect);
                break;
            case single:// 单机模式 传递默认default
            default: addresses.add(format);break;
        }
        return addresses;
    }

//    public static void main(String[] args) {
//        //使用脚本引擎（如JavaScript）: Java提供了一个javax.script包，你可以使用其中的ScriptEngine来执行JavaScript代码。通过这种方法，你可以动态地计算字符串中的表达式。
////        List<String> keys = null;keys.stream().collect(Collectors.toList());
//  /*      String expression = "1==1&&null!=1";
//        ScriptEngineManager manager = new ScriptEngineManager();
//        ScriptEngine engine = manager.getEngineByName("JavaScript");
//        try {
//            Object result = engine.eval(expression);
//            boolean booleanResult = (Boolean) result;
//            System.out.println(booleanResult); // 输出 true
//        } catch (ScriptException e) {
//            e.printStackTrace();
//        }
//
////表达式解析库: 使用第三方库如Janino或Apache Commons JEXL来解析和计算字符串形式的表达式。
//        String str = "1==1||null!=1";
//        boolean result = Boolean.parseBoolean(str);
//        System.out.println(result);
//
//        String template = "redis://%s:%s";
//        String format = String.format(template, "127.0.0.1", 6379);
//        System.out.println(template);
//        System.out.println(format);
//
//
//        Duration zero = Duration.ZERO;
//        String x = zero.toString();
//        System.out.println(x);
//        Duration parse = Duration.parse(x);
//        System.out.println(parse);
//*/
//        // 创建JEXL引擎
//        JexlEngine jexl = new JexlBuilder().create();
//
//        // 定义要计算的表达式字符串
//        String expression = "1!=1||null==1";
//
//        // 将表达式编译为JexlExpression对象
//        JexlExpression jexlExpression = jexl.createExpression(expression);
//
//        // 执行表达式并获取结果
//        // 由于这个表达式是一个条件表达式，它的结果将被封装在Boolean对象中
//        Boolean result = (Boolean) jexlExpression.evaluate(null);
//
//        // 打印结果
//        System.out.println(result); // 应该输出 true
//    }

}

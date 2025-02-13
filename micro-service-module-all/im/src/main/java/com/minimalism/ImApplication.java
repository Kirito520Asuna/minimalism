package com.minimalism;

//import com.minimalism.im.config.CommonConfig;
import com.minimalism.scan.SwaggerScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;


@SpringBootApplication
//@Import({CommonConfig.class, SwaggerScan.class})
//@ComponentScan(basePackages = {"com.parent.common.log","com.parent.common.exception"})
//@ComponentScan(excludeFilters = {
//        @ComponentScan.Filter(type = FilterType.REGEX,pattern = "com.yan.filter.CorsFilter")
//})
public class ImApplication /*implements CommandLineRunner*/ {
    /**
     * im 后端模板建立
     * @param args
     */
    //@Resource
    //private CoordinationNettyServer nettyServer;

    public static void main(String[] args) {
        SpringApplication.run(ImApplication.class, args);
    }


/*    @Override
    public void run(String... args) throws Exception {
        CompletableFuture.runAsync(()->{
            try {
                //nettyServer.start();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }*/
}

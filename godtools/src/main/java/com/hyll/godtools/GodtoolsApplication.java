package com.hyll.godtools;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;


@SpringBootApplication
@MapperScan("com.hyll.godtools.**.*mapper")
@EnableTransactionManagement
@EnableMethodCache(basePackages = "com.hyll.godtools")
@EnableCreateCacheAnnotation
@EnableScheduling
public class GodtoolsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GodtoolsApplication.class, args);
    }

}

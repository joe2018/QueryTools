package com.hyll.godtools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;


@SpringBootApplication
@MapperScan("com.hyll.godtools.mapper")
@EnableTransactionManagement
public class GodtoolsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GodtoolsApplication.class, args);
    }

}

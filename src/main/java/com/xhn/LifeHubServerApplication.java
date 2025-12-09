package com.xhn;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.xhn.**.**.mapper")
@SpringBootApplication
public class LifeHubServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LifeHubServerApplication.class, args);
    }

}

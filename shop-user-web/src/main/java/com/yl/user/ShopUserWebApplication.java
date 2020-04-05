package com.yl.user;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDubbo
@SpringBootApplication
public class ShopUserWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopUserWebApplication.class, args);
    }

}

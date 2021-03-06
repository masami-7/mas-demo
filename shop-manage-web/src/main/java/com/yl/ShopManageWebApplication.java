package com.yl;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDubbo
@SpringBootApplication
public class ShopManageWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopManageWebApplication.class, args);
    }

}

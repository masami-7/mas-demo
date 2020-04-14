package com.yl;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@EnableDubbo
@MapperScan(basePackages = "com.yl.mapper")
@ComponentScan(basePackages = "com.yl.config")
@SpringBootApplication
public class ShopManageServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopManageServiceApplication.class, args);
    }

}

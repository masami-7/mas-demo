package com.yl;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@EnableDubbo
@MapperScan(basePackages = "com.yl.mapper")
@SpringBootApplication
public class ShopPaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopPaymentApplication.class, args);
    }

}

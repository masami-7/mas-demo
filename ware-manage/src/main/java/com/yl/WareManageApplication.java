package com.yl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;


@MapperScan(basePackages = "com.yl.mapper")
@SpringBootApplication
public class WareManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(WareManageApplication.class, args);
    }

}

package com.zcyk;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import tk.mybatis.spring.annotation.MapperScan;



@SpringBootApplication
@MapperScan("com.zcyk.mapper")
@ServletComponentScan(basePackages = {"com.zcyk.filter"})
public class CloudUserManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudUserManageApplication.class, args);
    }

}

package com.zcyk;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;



@SpringBootApplication
@MapperScan("com.zcyk.mapper")
@ServletComponentScan
public class CloudUserManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudUserManageApplication.class, args);
    }

}

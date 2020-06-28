package com.zcyk.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
@EnableWebMvc
public class MyConfiguration implements WebMvcConfigurer {
    /* */

    @Value("${allowedOriginsIP}")
    String allowedOriginsIP;
    /**
     * SpringBoot设置首页
     *//*
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        WebMvcConfigurer.super.addViewControllers(registry);
        registry.addViewController("/").setViewName("login");
    }*/

    /* *
     * 添加允许任何网址跨域
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
//                .allowedOrigins("*").allowedMethods("*").maxAge(3600).allowCredentials(true);
                .allowedOrigins("*").allowedMethods("*").maxAge(3600).allowCredentials(true)
                .allowedHeaders("*")
                .exposedHeaders("user_token");

    }



    /**
     * 功能描述：跨域
     * 开发人员： lyx
     * 创建时间： 2019/8/15 9:58
     */
   /* @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry){
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("*")
                        .allowedHeaders("DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Content-Range,Range,Account_Token,login_code")
                        .exposedHeaders("DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Content-Range,Range,Account_Token,login_code")
                        .allowCredentials(true)
                        .maxAge(3600);
            }

        };
    }
*/

    /**
     * 静态文件映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //将所有/static/** 访问都映射到classpath:/static/ 目录下
        registry.addResourceHandler("/temp/**").addResourceLocations("file:D://home//cloud//file//temp//");
    }


}

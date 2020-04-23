package com.zcyk.Config;

import com.zcyk.filter.CustomMultipartResolver;
import com.zcyk.mapper.UserMapper;
import com.zcyk.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;

import javax.servlet.MultipartConfigElement;

/**
 * @author WuJieFeng
 * @date 2019/10/28 13:48
 */
@Configuration
public class BeanConfig {

/*zhes*/

    @Autowired
    private UserMapper userMapper;

    @Bean(name = "multipartResolver")
    public MultipartResolver multipartResolver()
    {
        return new CustomMultipartResolver();
    }


}

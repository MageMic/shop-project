package com.zju.wsh.user;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: zjumic
 * @Date: created at 23:45 2020/4/26
 */
@SpringBootApplication
@EnableDubboConfiguration
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class,args);
    }
}

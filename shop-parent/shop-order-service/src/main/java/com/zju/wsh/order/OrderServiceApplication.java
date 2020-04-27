package com.zju.wsh.order;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import com.zju.wsh.utils.IDWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @Author: zjumic
 * @Date: created at 23:48 2020/4/26
 */
@SpringBootApplication
@EnableDubboConfiguration
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class,args);
    }
    @Bean
    public IDWorker getBean(){
        return new IDWorker(1,1);
    }
}

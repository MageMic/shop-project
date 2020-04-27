package com.zju.wsh.goods;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: zjumic
 * @Date: created at 22:54 2020/4/26
 */
@SpringBootApplication
@EnableDubboConfiguration
public class GoodsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoodsServiceApplication.class, args);
    }
}

package com.zju.wsh.coupon;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: zjumic
 * @Date: created at 22:50 2020/4/26
 */
@SpringBootApplication
@EnableDubboConfiguration
public class CouponServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CouponServiceApplication.class, args);
    }
}

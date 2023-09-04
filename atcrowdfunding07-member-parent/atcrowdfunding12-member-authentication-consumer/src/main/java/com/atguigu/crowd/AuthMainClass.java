package com.atguigu.crowd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : LiuDongBin
 * @create 2023/7/14 19:52
 */
@EnableFeignClients
@SpringBootApplication
@EnableEurekaClient
public class AuthMainClass {
    //authentication-consumer-4001
    public static void main(String[] args) {
        SpringApplication.run(AuthMainClass.class, args);
    }
}

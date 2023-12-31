package com.atguigu.crowd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : LiuDongBin
 * @create 2023/9/2 14:22
 */
@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication
public class PayCrowdMain {
    public static void main(String[] args) {
        SpringApplication.run(PayCrowdMain.class, args);
    }
}

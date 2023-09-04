package com.atguigu.crowd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : LiuDongBin
 * @create 2023/7/11 16:03
 */
@EnableEurekaServer
@SpringBootApplication
public class EurekaMainClass {
    //Eureka-1001
    public static void main(String[] args) {
        SpringApplication.run(EurekaMainClass.class, args);
    }
}

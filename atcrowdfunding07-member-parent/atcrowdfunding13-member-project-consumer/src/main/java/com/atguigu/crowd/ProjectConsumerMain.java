package com.atguigu.crowd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * Created by IntelliJ IDEA.
 *
 * @Author : LiuDongBin
 * @create 2023/8/10 20:33
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class ProjectConsumerMain {
    public static void main(String[] args) {
        SpringApplication.run(ProjectConsumerMain.class, args);
    }
}

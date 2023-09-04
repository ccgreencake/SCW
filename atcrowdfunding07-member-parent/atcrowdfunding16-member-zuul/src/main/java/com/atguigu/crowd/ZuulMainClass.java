package com.atguigu.crowd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : LiuDongBin
 * @create 2023/7/14 23:28
 */
@SpringBootApplication
@EnableZuulProxy
public class ZuulMainClass {
    public static void main(String[] args) {
        //zuul-81
        SpringApplication.run(ZuulMainClass.class, args);
    }
}

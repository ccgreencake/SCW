package com.atguigu.crowd;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : LiuDongBin
 * @create 2023/7/12 16:29
 */
@MapperScan("com.atguigu.crowd.mapper")
@SpringBootApplication
public class MysqlMainClass {
    //Mysql-Provider-2001
    public static void main(String[] args) {
        SpringApplication.run(MysqlMainClass.class, args);
    }
}

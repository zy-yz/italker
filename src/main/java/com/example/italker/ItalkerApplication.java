package com.example.italker;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@MapperScan("com.example.italker.mapper")
@SpringBootApplication
@EnableTransactionManagement
public class ItalkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItalkerApplication.class, args);
    }

}

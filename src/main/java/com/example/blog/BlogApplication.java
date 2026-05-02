package com.example.blog;

import com.example.blog.config.FileStorageProperties;
import com.example.blog.config.JwtProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.example.blog.mapper")
@EnableConfigurationProperties({FileStorageProperties.class, JwtProperties.class})
@SpringBootApplication
public class BlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }
}

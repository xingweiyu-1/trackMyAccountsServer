package com.trackmycounts.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.trackmycounts.server.mapper")
public class TrackMyCountsServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrackMyCountsServerApplication.class, args);
    }
}

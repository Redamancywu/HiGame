package com.higame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class HiGameServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(HiGameServiceApplication.class, args);
    }
}
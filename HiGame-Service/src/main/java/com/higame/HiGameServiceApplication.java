package com.higame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "HiGame Service API",
        version = "1.0",
        description = "HiGame Service Backend API Documentation"
    )
)
public class HiGameServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(HiGameServiceApplication.class, args);
    }
}
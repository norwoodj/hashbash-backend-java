package com.johnmalcolmnorwood.hashbash.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


@SpringBootApplication(scanBasePackages = "com.johnmalcolmnorwood.hashbash")
public class HashbashEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(HashbashEngineApplication.class, args);
    }
}
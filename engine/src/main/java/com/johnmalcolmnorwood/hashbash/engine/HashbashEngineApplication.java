package com.johnmalcolmnorwood.hashbash.engine;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


@SpringBootApplication(scanBasePackages = "com.johnmalcolmnorwood.hashbash")
public class HashbashEngineApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(HashbashEngineApplication.class)
                .web(WebApplicationType.NONE)
                .build()
                .run(args);
    }
}
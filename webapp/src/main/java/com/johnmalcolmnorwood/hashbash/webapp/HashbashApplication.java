package com.johnmalcolmnorwood.hashbash.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "com.johnmalcolmnorwood.hashbash")
public class HashbashApplication {

    public static void main(String[] args) {
        SpringApplication.run(HashbashApplication.class, args);
    }
}

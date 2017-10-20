package com.johnmalcolmnorwood.hashbash.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "com.johnmalcolmnorwood.hashbash")
public class HashbashWebappApplication {

    public static void main(String[] args) {
        SpringApplication.run(HashbashWebappApplication.class, args);
    }
}

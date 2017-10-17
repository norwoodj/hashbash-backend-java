package com.johnmalcolmnorwood.hashbash.model.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@EntityScan(basePackages = "com.johnmalcolmnorwood.hashbash.model")
public class EntityConfig {
}

package com.johnmalcolmnorwood.hashbash.repository.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@Configuration
@EnableJpaRepositories(basePackages = "com.johnmalcolmnorwood.hashbash.repository")
public class RepositoryConfig {
}

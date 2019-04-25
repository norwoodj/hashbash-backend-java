package com.johnmalcolmnorwood.hashbash.engine.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ForkJoinPool;


@Configuration
public class RainbowTableSearchConfig {
    @Value("${hashbash.search.numThreads}")
    private int numThreads;

    @Bean("java.util.concurrent.ForkJoinPool-rainbowTableSearch")
    public ForkJoinPool rainbowTableSearchThreadPool() {
        return new ForkJoinPool(numThreads);
    }
}

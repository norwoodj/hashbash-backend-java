package com.johnmalcolmnorwood.hashbash.job.common.config;

import com.johnmalcolmnorwood.hashbash.job.common.utils.RainbowTableWrapper;
import com.johnmalcolmnorwood.hashbash.model.RainbowTable;
import com.johnmalcolmnorwood.hashbash.repository.RainbowTableRepository;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@StepScope
public class RainbowConfig {

    @Value("#{jobParameters['rainbowTableId']}")
    private short rainbowTableId;

    @Autowired
    private RainbowTableRepository rainbowTableRepository;

    @Bean
    @StepScope
    public RainbowTableWrapper generateJobRainbowTable() {
        RainbowTable rainbowTable = rainbowTableRepository.findOne(rainbowTableId);

        return RainbowTableWrapper.builder()
                .rainbowTable(rainbowTable)
                .build();
    }
}

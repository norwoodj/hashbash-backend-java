package com.johnmalcolmnorwood.hashbash.job.common.config;

import com.johnmalcolmnorwood.hashbash.job.common.utils.RainbowTableWrapper;
import com.johnmalcolmnorwood.hashbash.job.common.listener.RainbowTableProgressListener;
import com.johnmalcolmnorwood.hashbash.repository.RainbowChainRepository;
import com.johnmalcolmnorwood.hashbash.repository.RainbowTableRepository;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ListenerConfig {

    @Autowired
    private RainbowTableRepository rainbowTableRepository;

    @Autowired
    private RainbowChainRepository rainbowChainRepository;

    @Autowired
    private RainbowTableWrapper generateJobRainbowTableWrapper;

    @Bean(name = "org.springframework.batch.core.StepExecutionListener-rainbowTable")
    @StepScope
    public StepExecutionListener rainbowProgressListener() {
        return new RainbowTableProgressListener(
                generateJobRainbowTableWrapper.getRainbowTable(),
                rainbowTableRepository,
                rainbowChainRepository
        );
    }
}

package com.johnmalcolmnorwood.hashbash.job.generate.config;

import com.johnmalcolmnorwood.hashbash.job.generate.listener.RainbowTableProgressListener;
import com.johnmalcolmnorwood.hashbash.job.generate.utils.RainbowTableWrapper;
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
    private RainbowTableWrapper generateJobRainbowTableWrapper;

    @Bean(name = "org.springframework.batch.core.StepExecutionListener-generate")
    @StepScope
    public StepExecutionListener rainbowProgressListener() {
        return new RainbowTableProgressListener(
                generateJobRainbowTableWrapper.getRainbowTable(),
                rainbowTableRepository
        );
    }
}

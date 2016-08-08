package com.johnmalcolmnorwood.hashbash.job.generate.config;

import com.johnmalcolmnorwood.hashbash.job.generate.processor.RainbowChainGenerateProcessor;
import com.johnmalcolmnorwood.hashbash.job.generate.utils.RainbowTableWrapper;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ProcessorConfig {

    @Autowired
    private RainbowTableWrapper generateJobRainbowTableWrapper;

    @Bean(name = "org.springframework.batch.item.ItemProcessor-generate")
    @StepScope
    public RainbowChainGenerateProcessor rainbowChainGeneratorItemProcessor() {
        return new RainbowChainGenerateProcessor(generateJobRainbowTableWrapper.getRainbowTable());
    }
}

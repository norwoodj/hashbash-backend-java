package com.johnmalcolmnorwood.hashbash.job.generate.config;

import com.johnmalcolmnorwood.hashbash.job.common.reader.SupplierReader;
import com.johnmalcolmnorwood.hashbash.job.generate.utils.RainbowTableWrapper;
import com.johnmalcolmnorwood.hashbash.rainbow.util.RandomStringSupplier;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;


@Configuration
public class ReaderConfig {

    @Autowired
    private RainbowTableWrapper generateJobRainbowTableWrapper;

    @Bean(name = "org.springframework.batch.item.ItemReader-generate")
    @StepScope
    public ItemReader<String> rainbowChainStartReader() {
        Supplier<String> randomStringSupplier = new RandomStringSupplier(
                generateJobRainbowTableWrapper.getRainbowTable().getCharacterSet(),
                generateJobRainbowTableWrapper.getRainbowTable().getPasswordLength(),
                generateJobRainbowTableWrapper.getRainbowTable().getNumChains()
        );

        return new SupplierReader<>(randomStringSupplier);
    }
}

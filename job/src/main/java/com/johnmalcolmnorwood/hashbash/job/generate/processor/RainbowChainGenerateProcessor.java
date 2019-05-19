package com.johnmalcolmnorwood.hashbash.job.generate.processor;

import com.johnmalcolmnorwood.hashbash.job.common.utils.RainbowTableWrapper;
import com.johnmalcolmnorwood.hashbash.model.RainbowChain;
import com.johnmalcolmnorwood.hashbash.model.RainbowTable;
import com.johnmalcolmnorwood.hashbash.rainbow.function.HashFunctions;
import com.johnmalcolmnorwood.hashbash.rainbow.function.ReductionFunctionFamilies;
import com.johnmalcolmnorwood.hashbash.rainbow.service.RainbowChainGeneratorService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Component
@StepScope
public class RainbowChainGenerateProcessor implements ItemProcessor<String, RainbowChain> {

    private final RainbowTable rainbowTable;
    private final RainbowChainGeneratorService rainbowChainGeneratorService;
    private final Timer chainGenerationTimer;

    @Autowired
    public RainbowChainGenerateProcessor(
            RainbowTableWrapper rainbowTableWrapper,
            MeterRegistry meterRegistry
    ) {
        rainbowTable = rainbowTableWrapper.getRainbowTable();
        chainGenerationTimer = Timer.builder("rainbow.chain.generate")
                .tag("rainbow-table-id", String.valueOf(rainbowTable.getId()))
                .tag("chain-length", String.valueOf(rainbowTable.getChainLength()))
                .tag("hash-function", String.valueOf(rainbowTable.getHashFunction()))
                .register(meterRegistry);

        rainbowChainGeneratorService = new RainbowChainGeneratorService(
                HashFunctions.getHashFunctionByName(rainbowTable.getHashFunction()),
                ReductionFunctionFamilies.defaultReductionFunctionFamily(rainbowTable.getPasswordLength(), rainbowTable.getCharacterSet())
        );
    }

    @Override
    public RainbowChain process(String chainStart) throws Exception {
        var sample = Timer.start();
        RainbowChain rainbowChain = rainbowChainGeneratorService.generateRainbowChain(
                chainStart,
                rainbowTable.getChainLength()
        );

        rainbowChain.setRainbowTableId(rainbowTable.getId());
        sample.stop(chainGenerationTimer);
        return rainbowChain;
    }
}
